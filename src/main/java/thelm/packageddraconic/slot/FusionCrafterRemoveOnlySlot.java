package thelm.packageddraconic.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;

//Code from CoFHCore
public class FusionCrafterRemoveOnlySlot extends SlotItemHandler {

	public final FusionCrafterBlockEntity blockEntity;

	public FusionCrafterRemoveOnlySlot(FusionCrafterBlockEntity blockEntity, int index, int x, int y) {
		super(blockEntity.getItemHandler(), index, x, y);
		this.blockEntity = blockEntity;
	}

	@Override
	public boolean mayPickup(Player player) {
		return !blockEntity.isWorking;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}
}
