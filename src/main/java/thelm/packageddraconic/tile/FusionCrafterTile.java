package thelm.packageddraconic.tile;

import java.util.ArrayList;
import java.util.List;
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

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thelm.packagedauto.api.IPackageCraftingMachine;
import thelm.packagedauto.api.IPackageRecipeInfo;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.tile.BaseTile;
import thelm.packagedauto.tile.UnpackagerTile;
import thelm.packagedauto.util.MiscHelper;
import thelm.packageddraconic.block.FusionCrafterBlock;
import thelm.packageddraconic.client.fx.FusionCrafterFXHandler;
import thelm.packageddraconic.container.FusionCrafterContainer;
import thelm.packageddraconic.integration.appeng.tile.AEFusionCrafterTile;
import thelm.packageddraconic.inventory.FusionCrafterItemHandler;
import thelm.packageddraconic.network.packet.FinishCraftEffectsPacket;
import thelm.packageddraconic.network.packet.SyncCrafterPacket;
import thelm.packageddraconic.recipe.IFusionPackageRecipeInfo;

public class FusionCrafterTile extends BaseTile implements ITickableTileEntity, IPackageCraftingMachine, IFusionInventory, IFusionStateMachine {

	public static final TileEntityType<FusionCrafterTile> TYPE_INSTANCE = (TileEntityType<FusionCrafterTile>)TileEntityType.Builder.
			of(MiscHelper.INSTANCE.conditionalSupplier(()->ModList.get().isLoaded("appliedenergistics2"),
					()->AEFusionCrafterTile::new, ()->FusionCrafterTile::new), FusionCrafterBlock.INSTANCE).
			build(null).setRegistryName("packageddraconic:fusion_crafter");

	public static int energyCapacity = 5000;
	public static int energyUsage = 5;
	public static boolean drawMEEnergy = true;

	public Runnable fxHandler = DistExecutor.runForDist(()->()->new FusionCrafterFXHandler(this), ()->()->Runnables.doNothing());
	public IFusionRecipe effectRecipe;
	public float animProgress = 0;
	public short animLength = 0;
	public boolean isWorking = false;	
	public FusionState fusionState = FusionState.START;
	public int fusionCounter = 0;
	public short progress = 0;
	public int minTier = -1;
	public IFusionPackageRecipeInfo currentRecipe;
	public List<BlockPos> injectors = new ArrayList<>();

	public FusionCrafterTile() {
		super(TYPE_INSTANCE);
		setItemHandler(new FusionCrafterItemHandler(this));
		setEnergyStorage(new EnergyStorage(this, energyCapacity));
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("block.packageddraconic.fusion_crafter");
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
			energyStorage.updateIfChanged();
		}
		else {
			fxHandler.run();
		}
	}

	@Override
	public boolean acceptPackage(IPackageRecipeInfo recipeInfo, List<ItemStack> stacks, Direction direction) {
		if(!isBusy() && recipeInfo instanceof IFusionPackageRecipeInfo) {
			IFusionPackageRecipeInfo recipe = (IFusionPackageRecipeInfo)recipeInfo;
			List<ItemStack> injectorInputs = recipe.getInjectorInputs();
			List<BlockPos> emptyInjectors = getEmptyInjectors(recipe.getTierRequired());
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
					MarkedInjectorTile injector = (MarkedInjectorTile)craftInjectors.get(i);
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
						MarkedInjectorTile injector = (MarkedInjectorTile)craftInjectors.get(i);
						injector.setInjectorStack(ItemStack.EMPTY);
						injector.setCrafter(this);
					}
					return false;
				}
				syncTile(false);
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
		if(injectors.stream().map(level::getBlockEntity).
				anyMatch(tile->!(tile instanceof MarkedInjectorTile) || tile.isRemoved())) {
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
		filter(tile->tile instanceof MarkedInjectorTile && !tile.isRemoved()).
		forEach(tile->((MarkedInjectorTile)tile).spawnItem());
		injectors.clear();
		isWorking = false;
		minTier = -1;
		effectRecipe = null;
		currentRecipe = null;
		syncTile(false);
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
		List<BlockPos> positions = new ArrayList<>();
		int range = DEConfig.fusionInjectorRange;
		int radius = 1;
		List<MarkedInjectorTile> searchTiles = Streams.concat(
				BlockPos.betweenClosedStream(worldPosition.offset(-range, -radius, -radius), worldPosition.offset(range, radius, radius)),
				BlockPos.betweenClosedStream(worldPosition.offset(-radius, -range, -radius), worldPosition.offset(radius, range, radius)),
				BlockPos.betweenClosedStream(worldPosition.offset(-radius, -radius, -range), worldPosition.offset(radius, radius, range))).
				map(level::getBlockEntity).
				filter(t->t instanceof MarkedInjectorTile).
				map(t->(MarkedInjectorTile)t).
				collect(Collectors.toList());
		for(MarkedInjectorTile tile : searchTiles) {
			Vector3i dirVec = tile.getBlockPos().subtract(worldPosition);
			int dist = Ints.max(Math.abs(dirVec.getX()), Math.abs(dirVec.getY()), Math.abs(dirVec.getZ()));
			if(dist <= DEConfig.fusionInjectorMinDist) {
				positions.clear();
				return positions;
			}
			if(tile.getInjectorTier().index == tier && tile.getInjectorStack().isEmpty() &&
					Direction.getNearest(dirVec.getX(), dirVec.getY(), dirVec.getZ()) == tile.getDirection().getOpposite()) {
				BlockPos pos = tile.getBlockPos();
				Direction facing = tile.getDirection();
				boolean obstructed = false;
				for(BlockPos bp : BlockPos.betweenClosed(pos.relative(facing), pos.relative(facing, distanceInDirection(pos, worldPosition, facing) - 1))) {
					if(!level.isEmptyBlock(bp) && level.getBlockState(bp).canOcclude() || level.getBlockEntity(bp) instanceof MarkedInjectorTile || level.getBlockEntity(bp) instanceof FusionCrafterTile) {
						obstructed = true;
						break;
					}
				}
				if(!obstructed) {
					positions.add(tile.getBlockPos());
				}
			}
		}
		return positions;
	}

	@Override
	public List<IFusionInjector> getInjectors() {
		return injectors.stream().map(level::getBlockEntity).
				filter(tile->tile instanceof MarkedInjectorTile && !tile.isRemoved()).
				map(tile->(IFusionInjector)tile).collect(Collectors.toList());
	}

	public static int distanceInDirection(BlockPos fromPos, BlockPos toPos, Direction direction) {
		switch(direction) {
		case DOWN: return fromPos.getY() - toPos.getY();
		case UP: return toPos.getY() - fromPos.getY();
		case NORTH: return fromPos.getZ() - toPos.getZ();
		case SOUTH: return toPos.getZ() - fromPos.getZ();
		case WEST: return fromPos.getX() - toPos.getX();
		case EAST: return toPos.getX() - fromPos.getX();
		}
		return 0;
	}

	protected void ejectItems() {
		int endIndex = isWorking ? 1 : 0;
		for(Direction direction : Direction.values()) {
			TileEntity tile = level.getBlockEntity(worldPosition.relative(direction));
			if(tile != null && !(tile instanceof UnpackagerTile) && tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).isPresent()) {
				IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).resolve().get();
				boolean flag = true;
				for(int i = 1; i >= endIndex; --i) {
					ItemStack stack = this.itemHandler.getStackInSlot(i);
					if(stack.isEmpty()) {
						continue;
					}
					for(int slot = 0; slot < itemHandler.getSlots(); ++slot) {
						ItemStack stackRem = itemHandler.insertItem(slot, stack, false);
						if(stackRem.getCount() < stack.getCount()) {
							stack = stackRem;
							flag = false;
						}
						if(stack.isEmpty()) {
							break;
						}
					}
					this.itemHandler.setStackInSlot(i, stack);
					if(flag) {
						break;
					}
				}
			}
		}
	}

	protected void chargeEnergy() {
		int prevStored = energyStorage.getEnergyStored();
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
	public void setFusionStatus(double progress, ITextComponent stateText) {
		if(progress < 0) {
			this.progress = 0;
		}
		switch(fusionState) {
		case CHARGING:
			this.progress = (short)(progress*10000);
			break;
		case CRAFTING:
			this.progress = (short)(10000+progress*10000);
			break;
		default:
			this.progress = (short)(progress*20000);
			break;
		}
		setChanged();
	}

	@Override
	public void setCraftAnimation(float progress, int length) {
		animProgress = progress;
		animLength = (short)length;
		setChanged();
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		endProcess();
	}

	@Override
	public void load(BlockState blockState, CompoundNBT nbt) {
		super.load(blockState, nbt);
		fusionCounter = nbt.getInt("FusionCounter");
		currentRecipe = null;
		if(nbt.contains("Recipe")) {
			CompoundNBT tag = nbt.getCompound("Recipe");
			IPackageRecipeInfo recipe = MiscHelper.INSTANCE.readRecipe(tag);
			if(recipe instanceof IFusionPackageRecipeInfo) {
				currentRecipe = (IFusionPackageRecipeInfo)recipe;
			}
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("FusionCounter", fusionCounter);
		if(currentRecipe != null) {
			CompoundNBT tag = MiscHelper.INSTANCE.writeRecipe(new CompoundNBT(), currentRecipe);
			nbt.put("Recipe", tag);
		}
		return nbt;
	}

	@Override
	public void readSync(CompoundNBT nbt) {
		super.readSync(nbt);
		isWorking = nbt.getBoolean("Working");
		fusionState = FusionState.values()[nbt.getByte("FusionState")];
		progress = nbt.getShort("Progress");
		animProgress = nbt.getFloat("AnimProgress");
		animLength = nbt.getShort("AnimLength");
		itemHandler.read(nbt);
		injectors.clear();
		ListNBT injectorsTag = nbt.getList("Injectors", 11);
		for(int i = 0; i < injectorsTag.size(); ++i) {
			int[] posArray = injectorsTag.getIntArray(i);
			BlockPos pos = new BlockPos(posArray[0], posArray[1], posArray[2]);
			injectors.add(pos);
		}
		if(nbt.contains("EffectRecipe")) {
			IRecipe recipe = MiscHelper.INSTANCE.getRecipeManager().byKey(new ResourceLocation(nbt.getString("EffectRecipe"))).orElse(null);
			if(recipe instanceof IFusionRecipe) {
				effectRecipe = (IFusionRecipe)recipe;
			}
		}
	}

	@Override
	public CompoundNBT writeSync(CompoundNBT nbt) {
		super.writeSync(nbt);
		nbt.putBoolean("Working", isWorking);
		nbt.putByte("FusionState", (byte)fusionState.ordinal());
		nbt.putShort("Progress", progress);
		nbt.putFloat("AnimProgress", animProgress);
		nbt.putShort("AnimLength", animLength);
		itemHandler.write(nbt);
		ListNBT injectorsTag = new ListNBT();
		injectors.stream().map(pos->new int[] {pos.getX(), pos.getY(), pos.getZ()}).
		forEach(arr->injectorsTag.add(new IntArrayNBT(arr)));
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
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(worldPosition).inflate(16);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
		syncTile(false);
		return new FusionCrafterContainer(windowId, playerInventory, this);
	}
}
