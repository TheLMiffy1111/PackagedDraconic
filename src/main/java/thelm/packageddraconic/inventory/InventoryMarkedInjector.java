package thelm.packageddraconic.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import thelm.packagedauto.inventory.InventoryTileBase;
import thelm.packageddraconic.tile.TileMarkedInjector;

public class InventoryMarkedInjector extends InventoryTileBase {

	public final TileMarkedInjector tile;

	public InventoryMarkedInjector(TileMarkedInjector tile) {
		super(tile, 1);
		this.tile = tile;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack ret = super.decrStackSize(index, count);
		syncTile(false);
		return ret;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack ret = super.removeStackFromSlot(index);
		syncTile(false);
		return ret;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		super.setInventorySlotContents(index, stack);
		syncTile(false);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}
}
