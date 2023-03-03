package thelm.packageddraconic.energy;

import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;
import thelm.packageddraconic.tile.TileMarkedInjector;

public class EnergyStorageMarkedInjector implements IEnergyStorage {

	public final TileMarkedInjector tile;
	protected long energy;

	public EnergyStorageMarkedInjector(TileMarkedInjector tile) {
		this.tile = tile;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		tile.validateCraftingInventory();
		IFusionCraftingInventory inv = tile.getCraftingInventory();
		if(inv != null) {
			long ingCost = inv.getIngredientEnergyCost();
			long storageMaxReceive = ingCost/(300-tile.getPedestalTier()*80);
			int energyReceived = (int)Math.min(ingCost-energy, Math.min(storageMaxReceive, maxReceive));
			if(!simulate) {
				energy += energyReceived;
				tile.markDirty();
			}
			return energyReceived;
		}
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return (int)Math.min(Integer.MAX_VALUE, getExtendedEnergyStored());
	}

	public long getExtendedEnergyStored() {
		return energy;
	}

	@Override
	public int getMaxEnergyStored() {
		return (int)Math.min(Integer.MAX_VALUE, getExtendedMaxEnergyStored());
	}

	public long getExtendedMaxEnergyStored() {
		tile.validateCraftingInventory();
		IFusionCraftingInventory inv = tile.getCraftingInventory();
		return inv != null ? inv.getIngredientEnergyCost() : 0;
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	public EnergyStorageMarkedInjector readFromNBT(NBTTagCompound nbt) {
		energy = nbt.getLong("Energy");
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if(energy < 0) {
			energy = 0;
		}
		nbt.setLong("Energy", energy);
		return nbt;
	}

	public void setEnergyStored(long energy) {
		this.energy = energy;
		if(this.energy < 0) {
			this.energy = 0;
		}
	}

	public void modifyEnergyStored(long energy) {
		this.energy += energy;
		if(this.energy < 0) {
			this.energy = 0;
		}
	}
}
