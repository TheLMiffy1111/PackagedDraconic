package thelm.packageddraconic.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandlerModifiable;
import thelm.packagedauto.inventory.BaseItemHandler;
import thelm.packageddraconic.tile.FusionCrafterTile;

public class FusionCrafterItemHandler extends BaseItemHandler<FusionCrafterTile> {

	public FusionCrafterItemHandler(FusionCrafterTile tile) {
		super(tile, 3);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if(slot == 2) {
			return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
		}
		return false;
	}

	@Override
	public IItemHandlerModifiable getWrapperForDirection(Direction side) {
		return wrapperMap.computeIfAbsent(side, s->new FusionCrafterItemHandlerWrapper(this, s));
	}

	@Override
	public int get(int id) {
		switch(id) {
		case 0: return tile.progress;
		case 1: return tile.isWorking ? 1 : 0;
		case 2: return tile.getEnergyStorage().getEnergyStored();
		default: return 0;
		}
	}

	@Override
	public void set(int id, int value) {
		switch(id) {
		case 0:
			tile.progress = (short)value;
			break;
		case 1:
			tile.isWorking = value != 0;
			break;
		case 2:
			tile.getEnergyStorage().setEnergyStored(value);
			break;
		}
	}

	@Override
	public int getCount() {
		return 3;
	}
}
