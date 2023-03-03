package thelm.packageddraconic.block.entity;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import thelm.packagedauto.block.entity.BaseBlockEntity;
import thelm.packageddraconic.block.MarkedInjectorBlock;
import thelm.packageddraconic.inventory.MarkedInjectorItemHandler;
import thelm.packageddraconic.op.MarkedInjectorOPStorage;

public class MarkedInjectorBlockEntity extends BaseBlockEntity implements IFusionInjector {

	public static final BlockEntityType<MarkedInjectorBlockEntity> TYPE_INSTANCE = (BlockEntityType<MarkedInjectorBlockEntity>)BlockEntityType.Builder.
			of(MarkedInjectorBlockEntity::new, MarkedInjectorBlock.BASIC, MarkedInjectorBlock.WYVERN, MarkedInjectorBlock.DRACONIC, MarkedInjectorBlock.CHAOTIC).
			build(null).setRegistryName("packageddraconic:marked_injector");

	public MarkedInjectorOPStorage opStorage = new MarkedInjectorOPStorage(this);
	public BlockPos crafterPos = null;
	public int tier = -1;

	public MarkedInjectorBlockEntity(BlockPos pos, BlockState state) {
		super(TYPE_INSTANCE, pos, state);
		setItemHandler(new MarkedInjectorItemHandler(this));
	}

	@Override
	protected Component getDefaultName() {
		return getBlockState().getBlock().getName();
	}

	public void spawnItem() {
		ItemStack stack = itemHandler.getStackInSlot(0);
		itemHandler.setStackInSlot(0, ItemStack.EMPTY);
		if(!level.isClientSide && !stack.isEmpty()) {
			Direction direction = getDirection();
			double dx = level.random.nextFloat()/2+0.25+direction.getStepX()*0.5;
			double dy = level.random.nextFloat()/2+0.25+direction.getStepY()*0.5;
			double dz = level.random.nextFloat()/2+0.25+direction.getStepZ()*0.5;
			ItemEntity itemEntity = new ItemEntity(level, worldPosition.getX()+dx, worldPosition.getY()+dy, worldPosition.getZ()+dz, stack);
			itemEntity.setDefaultPickUpDelay();
			level.addFreshEntity(itemEntity);
		}
	}

	public boolean setCrafter(FusionCrafterBlockEntity crafter) {
		FusionCrafterBlockEntity oldCrafter = getCrafter();
		if(oldCrafter != null && oldCrafter != crafter && oldCrafter.isWorking) {
			oldCrafter.cancelCraft();
		}
		if(crafter == null) {
			crafterPos = null;
		}
		else {
			crafterPos = crafter.getBlockPos();
		}
		setEnergyRequirement(0, 0);
		sync(false);
		return true;
	}

	public FusionCrafterBlockEntity getCrafter() {
		if(crafterPos == null || level == null) {
			return null;
		}
		BlockEntity be = level.getBlockEntity(crafterPos);
		return be instanceof FusionCrafterBlockEntity crafter ? crafter : null;
	}

	@Override
	public TechLevel getInjectorTier() {
		if(tier == -1) {
			Block block = level.getBlockState(worldPosition).getBlock();
			if(block instanceof MarkedInjectorBlock) {
				tier = ((MarkedInjectorBlock)block).tier;
			}
		}
		return TechLevel.byIndex(tier);
	}

	@Override
	public ItemStack getInjectorStack() {
		return itemHandler.getStackInSlot(0);
	}

	@Override
	public void setInjectorStack(ItemStack stack) {
		itemHandler.setStackInSlot(0, stack);
	}

	@Override
	public long getInjectorEnergy() {
		return opStorage.energy;
	}

	@Override
	public void setInjectorEnergy(long energy) {
		opStorage.energy = energy;
		setChanged();
	}

	@Override
	public void setEnergyRequirement(long maxEnergy, long chargeRate) {
		opStorage.energyReq = maxEnergy;
		opStorage.chargeRate = chargeRate;
		setChanged();
	}

	@Override
	public long getEnergyRequirement() {
		return opStorage.energyReq;
	}

	@Override
	public boolean validate() {
		return !isRemoved() && level != null && level.getBlockEntity(worldPosition) == this;
	}

	public Direction getDirection() {
		BlockState state = level.getBlockState(worldPosition);
		if(state.getBlock() instanceof MarkedInjectorBlock) {
			return state.getValue(DirectionalBlock.FACING);
		}
		return Direction.UP;
	}

	@Override
	public void loadSync(CompoundTag nbt) {
		super.loadSync(nbt);
		itemHandler.load(nbt);
		opStorage.load(nbt);
		crafterPos = null;
		if(nbt.contains("CrafterPos")) {
			int[] posArray = nbt.getIntArray("CrafterPos");
			crafterPos = new BlockPos(posArray[0], posArray[1], posArray[2]);
		}
	}

	@Override
	public CompoundTag saveSync(CompoundTag nbt) {
		super.saveSync(nbt);
		itemHandler.save(nbt);
		opStorage.save(nbt);
		if(crafterPos != null) {
			nbt.putIntArray("CrafterPos", new int[] {crafterPos.getX(), crafterPos.getY(), crafterPos.getZ()});
		}
		return nbt;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction) {
		if(getDirection() != direction && (capability == CapabilityEnergy.ENERGY || capability == CapabilityOP.OP)) {
			return LazyOptional.of(()->(T)opStorage);
		}
		return super.getCapability(capability, direction);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return null;
	}
}
