package thelm.packageddraconic.container;

import net.minecraft.entity.player.InventoryPlayer;
import thelm.packagedauto.container.ContainerTileBase;
import thelm.packagedauto.slot.SlotBase;
import thelm.packagedauto.slot.SlotRemoveOnly;
import thelm.packageddraconic.slot.SlotFusionCrafterRemoveOnly;
import thelm.packageddraconic.tile.TileFusionCrafter;

public class ContainerFusionCrafter extends ContainerTileBase<TileFusionCrafter> {

	public ContainerFusionCrafter(InventoryPlayer playerInventory, TileFusionCrafter tile) {
		super(playerInventory, tile);
		addSlotToContainer(new SlotBase(inventory, 2, 8, 53));
		addSlotToContainer(new SlotFusionCrafterRemoveOnly(tile, 0, 53, 35));
		addSlotToContainer(new SlotRemoveOnly(inventory, 1, 107, 35));
		setupPlayerInventory();
	}
}
