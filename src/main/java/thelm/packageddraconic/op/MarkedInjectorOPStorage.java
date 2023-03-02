package thelm.packageddraconic.op;

import com.brandon3055.brandonscore.api.power.OPStorage;

import net.minecraft.nbt.CompoundTag;
import thelm.packageddraconic.block.entity.MarkedInjectorBlockEntity;
import thelm.packageddraconic.network.packet.SyncInjectorPacket;

public class MarkedInjectorOPStorage extends OPStorage {

	public final MarkedInjectorBlockEntity blockEntity;
	public long energy;
	public long energyReq;
	public long chargeRate;

	public MarkedInjectorOPStorage(MarkedInjectorBlockEntity blockEntity) {
		super(0);
		this.blockEntity = blockEntity;
	}

	@Override
	public long receiveOP(long maxReceive, boolean simulate) {
		long opStored = getOPStored();
		long received = Math.max(Math.min(getMaxOPStored()-opStored, Math.min(maxReceive, chargeRate)), 0);
		if(!simulate && received > 0) {
			energy += received;
			if(!blockEntity.getLevel().isClientSide) {
				blockEntity.setChanged();
				SyncInjectorPacket.sync(blockEntity);
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

	public MarkedInjectorOPStorage load(CompoundTag nbt) {
		energy = nbt.getLong("Energy");
		energyReq = nbt.getLong("EnergyReq");
		chargeRate = nbt.getLong("ChargeRate");
		return this;
	}

	public void save(CompoundTag nbt) {
		if(energy < 0) {
			energy = 0;
		}
		nbt.putLong("Energy", energy);
		nbt.putLong("EnergyReq", energyReq);
		nbt.putLong("ChargeRate", chargeRate);
	}

	public void setEnergyStored(long energy) {
		if(energy < 0) {
			energy = 0;
		}
		boolean flag = this.energy != energy;
		this.energy = energy;
		if(flag && !blockEntity.getLevel().isClientSide) {
			blockEntity.setChanged();
			SyncInjectorPacket.sync(blockEntity);
		}
	}
}
