package thelm.packageddraconic.menu;

import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.items.SlotItemHandler;
import thelm.packagedauto.menu.BaseMenu;
import thelm.packagedauto.slot.RemoveOnlySlot;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;
import thelm.packageddraconic.slot.FusionCrafterRemoveOnlySlot;

public class FusionCrafterMenu extends BaseMenu<FusionCrafterBlockEntity> {

	public FusionCrafterMenu(int windowId, Inventory inventory, FusionCrafterBlockEntity blockEntity) {
		super(PackagedDraconicMenus.FUSION_CRAFTER.get(), windowId, inventory, blockEntity);
		addSlot(new SlotItemHandler(itemHandler, 2, 8, 53));
		addSlot(new FusionCrafterRemoveOnlySlot(blockEntity, 0, 53, 35));
		addSlot(new RemoveOnlySlot(itemHandler, 1, 107, 35));
		setupPlayerInventory();
	}
}
