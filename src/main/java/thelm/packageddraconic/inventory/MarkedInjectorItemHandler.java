package thelm.packageddraconic.inventory;

import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.inventory.BaseItemHandler;
import thelm.packageddraconic.block.entity.MarkedInjectorBlockEntity;

public class MarkedInjectorItemHandler extends BaseItemHandler<MarkedInjectorBlockEntity> {

	public MarkedInjectorItemHandler(MarkedInjectorBlockEntity blockEntity) {
		super(blockEntity, 1);
	}

	@Override
	protected void onContentsChanged(int slot) {
		sync(false);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}
}
