package thelm.packageddraconic.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.items.SlotItemHandler;
import thelm.packagedauto.menu.BaseMenu;
import thelm.packagedauto.menu.factory.PositionalBlockEntityMenuFactory;
import thelm.packagedauto.slot.RemoveOnlySlot;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;
import thelm.packageddraconic.slot.FusionCrafterRemoveOnlySlot;

public class FusionCrafterMenu extends BaseMenu<FusionCrafterBlockEntity> {

	public static final MenuType<FusionCrafterMenu> TYPE_INSTANCE = (MenuType<FusionCrafterMenu>)IForgeMenuType.
			create(new PositionalBlockEntityMenuFactory<>(FusionCrafterMenu::new)).
			setRegistryName("packageddraconic:fusion_crafter");

	public FusionCrafterMenu(int windowId, Inventory inventory, FusionCrafterBlockEntity blockEntity) {
		super(TYPE_INSTANCE, windowId, inventory, blockEntity);
		addSlot(new SlotItemHandler(itemHandler, 2, 8, 53));
		addSlot(new FusionCrafterRemoveOnlySlot(blockEntity, 0, 53, 35));
		addSlot(new RemoveOnlySlot(itemHandler, 1, 107, 35));
		setupPlayerInventory();
	}
}
