package thelm.packageddraconic.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thelm.packagedauto.slot.SlotBase;
import thelm.packageddraconic.tile.TileFusionCrafter;

//Code from CoFHCore
public class SlotFusionCrafterRemoveOnly extends SlotBase {

	public final TileFusionCrafter tile;

	public SlotFusionCrafterRemoveOnly(TileFusionCrafter tile, int index, int x, int y) {
		super(tile.getInventory(), index, x, y);
		this.tile = tile;
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return !tile.isWorking;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
