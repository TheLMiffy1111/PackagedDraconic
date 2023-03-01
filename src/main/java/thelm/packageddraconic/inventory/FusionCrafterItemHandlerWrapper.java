package thelm.packageddraconic.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import thelm.packagedauto.inventory.SidedItemHandlerWrapper;

public class FusionCrafterItemHandlerWrapper extends SidedItemHandlerWrapper<FusionCrafterItemHandler> {

	public static final int[] SLOTS = {0, 1};

	public FusionCrafterItemHandlerWrapper(FusionCrafterItemHandler itemHandler, Direction direction) {
		super(itemHandler, direction);
	}

	@Override
	public int[] getSlotsForDirection(Direction direction) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, Direction direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, Direction direction) {
		return itemHandler.tile.isWorking ? index == 1 : true;
	}
}
