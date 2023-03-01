package thelm.packageddraconic.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.items.SlotItemHandler;
import thelm.packagedauto.container.BaseContainer;
import thelm.packagedauto.container.factory.PositionalTileContainerFactory;
import thelm.packagedauto.slot.RemoveOnlySlot;
import thelm.packageddraconic.slot.FusionCrafterRemoveOnlySlot;
import thelm.packageddraconic.tile.FusionCrafterTile;

public class FusionCrafterContainer extends BaseContainer<FusionCrafterTile> {

	public static final ContainerType<FusionCrafterContainer> TYPE_INSTANCE = (ContainerType<FusionCrafterContainer>)IForgeContainerType.
			create(new PositionalTileContainerFactory<>(FusionCrafterContainer::new)).
			setRegistryName("packageddraconic:fusion_crafter");

	public FusionCrafterContainer(int windowId, PlayerInventory playerInventory, FusionCrafterTile tile) {
		super(TYPE_INSTANCE, windowId, playerInventory, tile);
		addSlot(new SlotItemHandler(itemHandler, 2, 8, 53));
		addSlot(new FusionCrafterRemoveOnlySlot(tile, 0, 53, 35));
		addSlot(new RemoveOnlySlot(itemHandler, 1, 107, 35));
		setupPlayerInventory();
	}
}
