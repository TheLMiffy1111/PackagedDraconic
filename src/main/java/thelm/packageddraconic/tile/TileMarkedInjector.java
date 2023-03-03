package thelm.packageddraconic.tile;

import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingInjector;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import thelm.packagedauto.tile.TileBase;
import thelm.packageddraconic.block.BlockMarkedInjector;
import thelm.packageddraconic.energy.EnergyStorageMarkedInjector;
import thelm.packageddraconic.inventory.InventoryMarkedInjector;

public class TileMarkedInjector extends TileBase implements ICraftingInjector {

	public EnergyStorageMarkedInjector energyStorage = new EnergyStorageMarkedInjector(this);
	public BlockPos crafterPos = null;
	public int tier = -1;

	public TileMarkedInjector() {
		setInventory(new InventoryMarkedInjector(this));
	}

	@Override
	protected String getLocalizedName() {
		return getBlockType().getLocalizedName();
	}

	public void spawnItem() {
		ItemStack stack = inventory.getStackInSlot(0);
		inventory.setInventorySlotContents(0, ItemStack.EMPTY);
		if(!world.isRemote && !stack.isEmpty()) {
			EnumFacing facing = getDirection();
			double dx = world.rand.nextFloat()/2+0.25+facing.getXOffset()*0.5;
			double dy = world.rand.nextFloat()/2+0.25+facing.getYOffset()*0.5;
			double dz = world.rand.nextFloat()/2+0.25+facing.getZOffset()*0.5;
			EntityItem entityitem = new EntityItem(world, pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz, stack);
			entityitem.setDefaultPickupDelay();
			world.spawnEntity(entityitem);
		}
	}

	@Override
	public int getPedestalTier() {
		if(tier == -1) {
			Block block = getBlockType();
			if(block instanceof BlockMarkedInjector) {
				tier = ((BlockMarkedInjector)block).tier;
			}
		}
		return tier;
	}

	@Override
	public ItemStack getStackInPedestal() {
		return inventory.getStackInSlot(0);
	}

	@Override
	public void setStackInPedestal(ItemStack stack) {
		inventory.setInventorySlotContents(0, stack);
	}

	@Override
	public boolean setCraftingInventory(IFusionCraftingInventory craftingInventory) {
		if(craftingInventory == null) {
			crafterPos = null;
			return false;
		}
		if(validateCraftingInventory() && !world.isRemote) {
			return false;
		}
		crafterPos = ((TileEntity)craftingInventory).getPos();
		return true;
	}

	public IFusionCraftingInventory getCraftingInventory() {
		validateCraftingInventory();
		if(crafterPos != null) {
			TileEntity tile = world.getTileEntity(crafterPos);
			if(tile instanceof IFusionCraftingInventory) {
				return (IFusionCraftingInventory)tile;
			}
		}
		return null;
	}

	@Override
	public EnumFacing getDirection() {
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() instanceof BlockMarkedInjector) {
			return state.getValue(BlockDirectional.FACING);
		}
		return EnumFacing.UP;
	}

	@Override
	public long getInjectorCharge() {
		return energyStorage.getExtendedEnergyStored();
	}

	public boolean validateCraftingInventory() {
		if(!getStackInPedestal().isEmpty() && crafterPos != null) {
			TileEntity tile = world.getTileEntity(crafterPos);
			if(!tile.isInvalid() && tile instanceof IFusionCraftingInventory && ((IFusionCraftingInventory)tile).craftingInProgress()) {
				return true;
			}
		}
		crafterPos = null;
		return false;
	}

	@Override
	public void onCraft() {
		if(crafterPos != null) {
			energyStorage.setEnergyStored(0);
			crafterPos = null;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		energyStorage.readFromNBT(nbt);
		crafterPos = null;
		if(nbt.hasKey("CrafterPos")) {
			int[] posArray = nbt.getIntArray("CrafterPos");
			crafterPos = new BlockPos(posArray[0], posArray[1], posArray[2]);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		energyStorage.writeToNBT(nbt);
		if(crafterPos != null) {
			nbt.setIntArray("CrafterPos", new int[] {crafterPos.getX(), crafterPos.getY(), crafterPos.getZ()});
		}
		return nbt;
	}

	@Override
	public void readSyncNBT(NBTTagCompound nbt) {
		super.readSyncNBT(nbt);
		inventory.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
		super.writeSyncNBT(nbt);
		inventory.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {
		return capability == CapabilityEnergy.ENERGY && getDirection() != from || super.hasCapability(capability, from);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == CapabilityEnergy.ENERGY && getDirection() != facing ? (T)energyStorage : super.getCapability(capability, facing);
	}

	@Override
	public GuiContainer getClientGuiElement(EntityPlayer player, Object... args) {
		return null;
	}

	@Override
	public Container getServerGuiElement(EntityPlayer player, Object... args) {
		return null;
	}
}
