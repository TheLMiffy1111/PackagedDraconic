package thelm.packageddraconic.op;

import com.brandon3055.brandonscore.api.power.OPStorage;

import net.minecraft.nbt.CompoundNBT;
import thelm.packageddraconic.network.packet.SyncInjectorPacket;
import thelm.packageddraconic.tile.MarkedInjectorTile;

public class MarkedInjectorOPStorage extends OPStorage {

	public final MarkedInjectorTile tile;
	public long energy;
	public long energyReq;
	public long chargeRate;

	public MarkedInjectorOPStorage(MarkedInjectorTile tile) {
		super(0);
		this.tile = tile;
	}

	@Override
	public long receiveOP(long maxReceive, boolean simulate) {
		long opStored = getOPStored();
		long received = Math.max(Math.min(getMaxOPStored()-opStored, Math.min(maxReceive, chargeRate)), 0);
		if(!simulate && received > 0) {
			energy += received;
			if(!tile.getLevel().isClientSide) {
				tile.setChanged();
				SyncInjectorPacket.sync(tile);
			}
		}
		return received;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public long getOPStored() {
		return energy;
	}

	@Override
	public long getMaxOPStored() {
		return energyReq;
	}

	public MarkedInjectorOPStorage read(CompoundNBT nbt) {
		energy = nbt.getLong("Energy");
		energyReq = nbt.getLong("EnergyReq");
		chargeRate = nbt.getLong("ChargeRate");
		return this;
	}

	public CompoundNBT write(CompoundNBT nbt) {
		if(energy < 0) {
			energy = 0;
		}
		nbt.putLong("Energy", energy);
		nbt.putLong("EnergyReq", energyReq);
		nbt.putLong("ChargeRate", chargeRate);
		return nbt;
	}

	public void setEnergyStored(long energy) {
		if(energy < 0) {
			energy = 0;
		}
		boolean flag = this.energy != energy;
		this.energy = energy;
		if(flag && !tile.getLevel().isClientSide) {
			tile.setChanged();
			SyncInjectorPacket.sync(tile);
		}
	}
}
