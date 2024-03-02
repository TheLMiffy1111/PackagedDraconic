package thelm.packageddraconic.block.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;
import com.brandon3055.draconicevolution.api.crafting.IFusionInventory;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionStateMachine;
import com.google.common.collect.Streams;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.Runnables;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thelm.packagedauto.api.IPackageCraftingMachine;
import thelm.packagedauto.api.IPackageRecipeInfo;
import thelm.packagedauto.block.entity.BaseBlockEntity;
import thelm.packagedauto.block.entity.UnpackagerBlockEntity;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.util.MiscHelper;
import thelm.packageddraconic.block.FusionCrafterBlock;
import thelm.packageddraconic.client.fx.FusionCrafterFXHandler;
import thelm.packageddraconic.integration.appeng.blockentity.AEFusionCrafterBlockEntity;
import thelm.packageddraconic.inventory.FusionCrafterItemHandler;
import thelm.packageddraconic.menu.FusionCrafterMenu;
import thelm.packageddraconic.network.packet.FinishCraftEffectsPacket;
import thelm.packageddraconic.network.packet.SyncCrafterPacket;
import thelm.packageddraconic.recipe.IFusionPackageRecipeInfo;

public class FusionCrafterBlockEntity extends BaseBlockEntity implements IPackageCraftingMachine, IFusionInventory, IFusionStateMachine {

	public static final BlockEntityType<FusionCrafterBlockEntity> TYPE_INSTANCE = (BlockEntityType<FusionCrafterBlockEntity>)BlockEntityType.Builder.
			of(MiscHelper.INSTANCE.<BlockEntityType.BlockEntitySupplier<FusionCrafterBlockEntity>>conditionalSupplier(
					()->ModList.get().isLoaded("ae2"),
					()->()->AEFusionCrafterBlockEntity::new, ()->()->FusionCrafterBlockEntity::new).get(),
					FusionCrafterBlock.INSTANCE).
			build(null).setRegistryName("packageddraconic:fusion_crafter");

	public static int energyCapacity = 5000;
	public static int energyUsage = 5;
	public static boolean drawMEEnergy = true;

	public Runnable fxHandler = DistExecutor.runForDist(()->()->new FusionCrafterFXHandler(this), ()->()->Runnables.doNothing());
	public IFusionRecipe effectRecipe;
	public float animProgress = 0;
	public short animLength = 0;
	public int[] requiredInjectors = {0, 0, 0, 0};
	public boolean isWorking = false;	
	public FusionState fusionState = FusionState.START;
	public int fusionCounter = 0;
	public short progress = 0;
	public int minTier = -1;
	public IFusionPackageRecipeInfo currentRecipe;
	public List<BlockPos> injectors = new ArrayList<>();

	public FusionCrafterBlockEntity(BlockPos pos, BlockState state) {
		super(TYPE_INSTANCE, pos, state);
		setItemHandler(new FusionCrafterItemHandler(this));
		setEnergyStorage(new EnergyStorage(this, energyCapacity));
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("block.packageddraconic.fusion_crafter");
	}

	public Component getMessage() {
		if(isWorking) {
			return null;
		}
		MutableComponent message = new TranslatableComponent("block.packageddraconic.fusion_crafter.injectors.usable");
		MutableComponent usable = new TextComponent(" ");
		for(int i = 0; i <= 3; ++i) {
			int usableInjectors = getEmptyInjectorsForTier(i).size();
			if(usableInjectors > 0) {
				if(!usable.getSiblings().isEmpty()) {
					usable.append(", ");
				}
				usable.append(new TranslatableComponent("block.packageddraconic.fusion_crafter.injectors."+i, usableInjectors));
			}
		}
		if(usable.getSiblings().isEmpty()) {
			message.append(" 0");
		}
		else {
			message.append("\n");
			message.append(usable);
		}
		if(Arrays.stream(requiredInjectors).anyMatch(i->i > 0)) {
			message.append("\n");
			message.append(new TranslatableComponent("block.packageddraconic.fusion_crafter.injectors.required"));
			int[] actualRequiredInjectors = {
					requiredInjectors[0]-requiredInjectors[1]-requiredInjectors[2]-requiredInjectors[3],
					requiredInjectors[1]-requiredInjectors[2]-requiredInjectors[3],
					requiredInjectors[2]-requiredInjectors[3],
					requiredInjectors[3]
			};
			MutableComponent required = new TextComponent(" ");
			for(int i = 0; i <= 3; ++i) {
				int requiredInjectors = actualRequiredInjectors[i];
				if(requiredInjectors > 0) {
					if(!required.getSiblings().isEmpty()) {
						required.append(", ");
					}
					required.append(new TranslatableComponent("block.packageddraconic.fusion_crafter.injectors."+i, requiredInjectors));
				}
			}
			message.append("\n");
			message.append(required);
		}
		return message;
	}

	@Override
	public void tick() {
		if(!level.isClientSide) {
			if(isWorking) {
				tickProcess();
			}
			chargeEnergy();
			if(level.getGameTime() % 8 == 0) {
				ejectItems();
			}
		}
		else {
			fxHandler.run();
		}
	}

	@Override
	public boolean acceptPackage(IPackageRecipeInfo recipeInfo, List<ItemStack> stacks, Direction direction) {
		if(!isBusy() && recipeInfo instanceof IFusionPackageRecipeInfo recipe) {
			int tier = recipe.getTierRequired();
			List<ItemStack> injectorInputs = recipe.getInjectorInputs();
			List<BlockPos> emptyInjectors = getEmptyInjectors(tier);
			requiredInjectors[tier] = Math.max(requiredInjectors[tier], injectorInputs.size());
			if(emptyInjectors.size() >= injectorInputs.size()) {
				injectors.clear();
				injectors.addAll(emptyInjectors.subList(0, injectorInputs.size()));
				currentRecipe = recipe;
				effectRecipe = recipe.getRecipe();
				isWorking = true;
				fusionState = FusionState.START;
				itemHandler.setStackInSlot(0, recipe.getCoreInput().copy());
				List<IFusionInjector> craftInjectors = getInjectors();
				for(int i = 0; i < craftInjectors.size(); ++i) {
					MarkedInjectorBlockEntity injector = (MarkedInjectorBlockEntity)craftInjectors.get(i);
					injector.setInjectorStack(injectorInputs.get(i).copy());
					injector.setCrafter(this);
				}
				if(!recipe.getRecipe().matches(this, level) || !recipe.getRecipe().canStartCraft(this, level, t->{})) {
					injectors.clear();
					currentRecipe = null;
					effectRecipe = null;
					isWorking = false;
					itemHandler.setStackInSlot(0, ItemStack.EMPTY);
					for(int i = 0; i < craftInjectors.size(); ++i) {
						MarkedInjectorBlockEntity injector = (MarkedInjectorBlockEntity)craftInjectors.get(i);
						injector.setInjectorStack(ItemStack.EMPTY);
						injector.setCrafter(this);
					}
					return false;
				}
				sync(false);
				setChanged();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isBusy() {
		return isWorking || !itemHandler.getStacks().subList(0, 2).stream().allMatch(ItemStack::isEmpty);
	}

	protected void tickProcess() {
		if(injectors.stream().map(level::getBlockEntity).anyMatch(be->!(be instanceof MarkedInjectorBlockEntity) || be.isRemoved())) {
			cancelCraft();
		}
		else {
			SyncCrafterPacket.sync(this);
			if(fusionState.ordinal() < FusionState.CRAFTING.ordinal()) {
				currentRecipe.getRecipe().tickFusionState(this, this, level);
			}
			else if(energyStorage.extractEnergy(energyUsage, true) == energyUsage) {
				energyStorage.extractEnergy(energyUsage, false);
				currentRecipe.getRecipe().tickFusionState(this, this, level);
			}
		}
	}

	public void endProcess() {
		fusionCounter = 0;
		progress = 0;
		animProgress = 0;
		animLength = 0;
		injectors.stream().map(level::getBlockEntity).
		filter(be->be instanceof MarkedInjectorBlockEntity && !be.isRemoved()).
		forEach(be->((MarkedInjectorBlockEntity)be).ejectItem());
		injectors.clear();
		isWorking = false;
		minTier = -1;
		effectRecipe = null;
		currentRecipe = null;
		sync(false);
		setChanged();
	}

	protected List<BlockPos> getEmptyInjectors(int minTier) {
		List<BlockPos> positions = new ArrayList<>();
		for(int i = 3; i >= minTier; --i) {
			positions.addAll(getEmptyInjectorsForTier(i));
		}
		return positions;
	}

	protected List<BlockPos> getEmptyInjectorsForTier(int tier) {
		int range = DEConfig.fusionInjectorRange;
		int radius = 1;
		return Streams.concat(
				BlockPos.betweenClosedStream(worldPosition.offset(-range, -radius, -radius), worldPosition.offset(range, radius, radius)),
				BlockPos.betweenClosedStream(worldPosition.offset(-radius, -range, -radius), worldPosition.offset(radius, range, radius)),
				BlockPos.betweenClosedStream(worldPosition.offset(-radius, -radius, -range), worldPosition.offset(radius, radius, range))).
				map(checkPos->{
					BlockEntity be = level.getBlockEntity(checkPos);
					if(be instanceof MarkedInjectorBlockEntity injector) {
						Vec3i dirVec = checkPos.subtract(worldPosition);
						int dist = Ints.max(Math.abs(dirVec.getX()), Math.abs(dirVec.getY()), Math.abs(dirVec.getZ()));
						if(dist > DEConfig.fusionInjectorMinDist && injector.getInjectorTier().index == tier && injector.getInjectorStack().isEmpty() &&
								Direction.getNearest(dirVec.getX(), dirVec.getY(), dirVec.getZ()) == injector.getDirection().getOpposite()) {
							Direction facing = injector.getDirection();
							for(BlockPos bp : BlockPos.betweenClosed(
									checkPos.relative(facing),
									checkPos.relative(facing, distanceInDirection(checkPos, worldPosition, facing)-1))) {
								if(!level.isEmptyBlock(bp) && level.getBlockState(bp).canOcclude() || level.getBlockEntity(bp) instanceof MarkedInjectorBlockEntity || level.getBlockEntity(bp) instanceof FusionCrafterBlockEntity) {
									return null;
								}
							}
							return checkPos.immutable();
						}
					}
					return null;
				}).filter(Objects::nonNull).toList();
	}

	@Override
	public List<IFusionInjector> getInjectors() {
		return injectors.stream().map(level::getBlockEntity).
				filter(be->be instanceof MarkedInjectorBlockEntity && !be.isRemoved()).
				map(be->(IFusionInjector)be).collect(Collectors.toList());
	}

	public static int distanceInDirection(BlockPos fromPos, BlockPos toPos, Direction direction) {
		return switch(direction) {
		case DOWN -> fromPos.getY() - toPos.getY();
		case UP -> toPos.getY() - fromPos.getY();
		case NORTH -> fromPos.getZ() - toPos.getZ();
		case SOUTH -> toPos.getZ() - fromPos.getZ();
		case WEST -> fromPos.getX() - toPos.getX();
		case EAST -> toPos.getX() - fromPos.getX();
		default -> 0;
		};
	}

	protected void ejectItems() {
		int endIndex = isWorking ? 1 : 0;
		for(Direction direction : Direction.values()) {
			BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));
			if(blockEntity != null && !(blockEntity instanceof UnpackagerBlockEntity) && blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).isPresent()) {
				IItemHandler itemHandler = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).resolve().get();
				for(int i = 1; i >= endIndex; --i) {
					ItemStack stack = this.itemHandler.getStackInSlot(i);
					if(stack.isEmpty()) {
						continue;
					}
					ItemStack stackRem = ItemHandlerHelper.insertItem(itemHandler, stack, false);
					this.itemHandler.setStackInSlot(i, stackRem);
				}
			}
		}
	}

	protected void chargeEnergy() {
		ItemStack energyStack = itemHandler.getStackInSlot(2);
		if(energyStack.getCapability(CapabilityEnergy.ENERGY, null).isPresent()) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(energyStack.getCapability(CapabilityEnergy.ENERGY).resolve().get().extractEnergy(energyRequest, false), false);
			if(energyStack.getCount() <= 0) {
				itemHandler.setStackInSlot(2, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public ItemStack getCatalystStack() {
		return itemHandler.getStackInSlot(0);
	}

	@Override
	public ItemStack getOutputStack() {
		return itemHandler.getStackInSlot(1);
	}

	@Override
	public void setCatalystStack(ItemStack stack) {
		itemHandler.setStackInSlot(0, stack);
	}

	@Override
	public void setOutputStack(ItemStack stack) {
		itemHandler.setStackInSlot(1, stack);
	}

	@Override
	public TechLevel getMinimumTier() {
		if(minTier == -1) {
			minTier = getInjectors().stream().mapToInt(c->c.getInjectorTier().index).min().orElse(-1);
		}
		return TechLevel.byIndex(minTier);
	}

	@Override
	public FusionState getFusionState() {
		return fusionState;
	}

	@Override
	public void setFusionState(FusionState state) {
		fusionState = state;
		setChanged();
	}

	@Override
	public void completeCraft() {
		isWorking = false;
		getInjectors().forEach(e->e.setEnergyRequirement(0, 0));
		FinishCraftEffectsPacket.finishCraft(this, true);
		ejectItems();
		endProcess();
	}

	@Override
	public void cancelCraft() {
		isWorking = false;
		getInjectors().forEach(e->e.setEnergyRequirement(0, 0));
		FinishCraftEffectsPacket.finishCraft(this, false);
		ejectItems();
		endProcess();
	}

	@Override
	public int getCounter() {
		return fusionCounter;
	}

	@Override
	public void setCounter(int count) {
		fusionCounter = count;
		setChanged();
	}

	@Override
	public void setFusionStatus(double progress, Component stateText) {
		if(progress < 0) {
			this.progress = 0;
		}
		this.progress = switch(fusionState) {
		case CHARGING -> (short)(progress*10000);
		case CRAFTING -> (short)(10000+progress*10000);
		default -> (short)(progress*20000);
		};
		setChanged();
	}

	@Override
	public void setCraftAnimation(float progress, int length) {
		animProgress = progress;
		animLength = (short)length;
		setChanged();
	}

	@Override
	public int getComparatorSignal() {
		if(isWorking) {
			return 1;
		}
		if(!itemHandler.getStacks().subList(0, 2).stream().allMatch(ItemStack::isEmpty)) {
			return 15;
		}
		return 0;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		fusionState = FusionState.values()[nbt.getByte("FusionState")];
		progress = nbt.getShort("Progress");
		animProgress = nbt.getFloat("AnimProgress");
		animLength = nbt.getShort("AnimLength");
		fusionCounter = nbt.getInt("FusionCounter");
		currentRecipe = null;
		if(nbt.contains("Recipe")) {
			CompoundTag tag = nbt.getCompound("Recipe");
			IPackageRecipeInfo recipe = MiscHelper.INSTANCE.loadRecipe(tag);
			if(recipe instanceof IFusionPackageRecipeInfo fusionRecipe) {
				currentRecipe = fusionRecipe;
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putByte("FusionState", (byte)fusionState.ordinal());
		nbt.putShort("Progress", progress);
		nbt.putFloat("AnimProgress", animProgress);
		nbt.putShort("AnimLength", animLength);
		nbt.putInt("FusionCounter", fusionCounter);
		if(currentRecipe != null) {
			CompoundTag tag = MiscHelper.INSTANCE.saveRecipe(new CompoundTag(), currentRecipe);
			nbt.put("Recipe", tag);
		}
	}

	@Override
	public void loadSync(CompoundTag nbt) {
		super.loadSync(nbt);
		isWorking = nbt.getBoolean("Working");
		itemHandler.load(nbt);
		injectors.clear();
		ListTag injectorsTag = nbt.getList("Injectors", 11);
		for(int i = 0; i < injectorsTag.size(); ++i) {
			int[] posArray = injectorsTag.getIntArray(i);
			BlockPos pos = new BlockPos(posArray[0], posArray[1], posArray[2]);
			injectors.add(pos);
		}
		if(nbt.contains("EffectRecipe")) {
			Recipe<?> recipe = MiscHelper.INSTANCE.getRecipeManager().byKey(new ResourceLocation(nbt.getString("EffectRecipe"))).orElse(null);
			if(recipe instanceof IFusionRecipe fusionRecipe) {
				effectRecipe = fusionRecipe;
			}
		}
	}

	@Override
	public CompoundTag saveSync(CompoundTag nbt) {
		super.saveSync(nbt);
		nbt.putBoolean("Working", isWorking);
		itemHandler.save(nbt);
		ListTag injectorsTag = new ListTag();
		injectors.stream().map(pos->new int[] {pos.getX(), pos.getY(), pos.getZ()}).
		forEach(arr->injectorsTag.add(new IntArrayTag(arr)));
		nbt.put("Injectors", injectorsTag);
		if(effectRecipe != null) {
			nbt.putString("EffectRecipe", effectRecipe.getId().toString());
		}
		return nbt;
	}

	public int getScaledEnergy(int scale) {
		if(energyStorage.getMaxEnergyStored() <= 0) {
			return 0;
		}
		return Math.min(scale * energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored(), scale);
	}

	public int getScaledProgress(int scale) {
		if(progress <= 0) {
			return 0;
		}
		return scale * progress / 20000;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition).inflate(16);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		sync(false);
		return new FusionCrafterMenu(windowId, inventory, this);
	}
}
