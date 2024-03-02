package thelm.packageddraconic.tile;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.ModList;
import thelm.packagedauto.tile.BaseTile;
import thelm.packagedauto.util.MiscHelper;
import thelm.packageddraconic.block.MarkedInjectorBlock;
import thelm.packageddraconic.integration.appeng.tile.AEMarkedInjectorTile;
import thelm.packageddraconic.inventory.MarkedInjectorItemHandler;
import thelm.packageddraconic.op.MarkedInjectorOPStorage;

public class MarkedInjectorTile extends BaseTile implements ITickableTileEntity, IFusionInjector {

	public static final TileEntityType<MarkedInjectorTile> TYPE_INSTANCE = (TileEntityType<MarkedInjectorTile>)TileEntityType.Builder.
			of(MiscHelper.INSTANCE.conditionalSupplier(()->ModList.get().isLoaded("appliedenergistics2"),
					()->AEMarkedInjectorTile::new, ()->MarkedInjectorTile::new),
					MarkedInjectorBlock.BASIC, MarkedInjectorBlock.WYVERN, MarkedInjectorBlock.DRACONIC, MarkedInjectorBlock.CHAOTIC).
			build(null).setRegistryName("packageddraconic:marked_injector");

	public MarkedInjectorOPStorage opStorage = new MarkedInjectorOPStorage(this);
	public BlockPos crafterPos = null;
	public int tier = -1;

	public MarkedInjectorTile() {
		super(TYPE_INSTANCE);
		setItemHandler(new MarkedInjectorItemHandler(this));
	}

	@Override
	protected ITextComponent getDefaultName() {
		return getBlockState().getBlock().getName();
	}

	@Override
	public void tick() {}

	public void ejectItem() {
		ItemStack stack = itemHandler.getStackInSlot(0);
		itemHandler.setStackInSlot(0, ItemStack.EMPTY);
		if(!stack.isEmpty()) {
			Direction direction = getDirection();
			double dx = level.random.nextFloat()/2+0.25+direction.getStepX()*0.5;
			double dy = level.random.nextFloat()/2+0.25+direction.getStepY()*0.5;
			double dz = level.random.nextFloat()/2+0.25+direction.getStepZ()*0.5;
			ItemEntity itemEntity = new ItemEntity(level, worldPosition.getX()+dx, worldPosition.getY()+dy, worldPosition.getZ()+dz, stack);
			itemEntity.setDefaultPickUpDelay();
			level.addFreshEntity(itemEntity);
		}
	}

	public boolean setCrafter(FusionCrafterTile crafter) {
		FusionCrafterTile oldCrafter = getCrafter();
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
		syncTile(false);
		return true;
	}

	public FusionCrafterTile getCrafter() {
		if(crafterPos == null || level == null) {
			return null;
		}
		TileEntity tile = level.getBlockEntity(crafterPos);
		return tile instanceof FusionCrafterTile ? (FusionCrafterTile)tile : null;
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
	public int getComparatorSignal() {
		return itemHandler.getStackInSlot(0).isEmpty() ? 0 : 15;
	}

	@Override
	public void load(BlockState blockState, CompoundNBT nbt) {
		super.load(blockState, nbt);
		opStorage.read(nbt);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		opStorage.write(nbt);
		return nbt;
	}

	@Override
	public void readSync(CompoundNBT nbt) {
		super.readSync(nbt);
		itemHandler.read(nbt);
		crafterPos = null;
		if(nbt.contains("CrafterPos")) {
			int[] posArray = nbt.getIntArray("CrafterPos");
			crafterPos = new BlockPos(posArray[0], posArray[1], posArray[2]);
		}
	}

	@Override
	public CompoundNBT writeSync(CompoundNBT nbt) {
		super.writeSync(nbt);
		itemHandler.write(nbt);
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
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
		return null;
	}
}
