package thelm.packageddraconic.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thelm.packageddraconic.block.PackagedDraconicBlocks;

public class PackagedDraconicItems {

	private PackagedDraconicItems() {}

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("packageddraconic");

	public static final DeferredItem<?> FUSION_CRAFTER = ITEMS.registerSimpleBlockItem(PackagedDraconicBlocks.FUSION_CRAFTER);
	public static final DeferredItem<?> MARKED_DRACONIUM_INJECTOR = ITEMS.registerSimpleBlockItem(PackagedDraconicBlocks.MARKED_DRACONIUM_INJECTOR);
	public static final DeferredItem<?> MARKED_WYVERN_INJECTOR = ITEMS.registerSimpleBlockItem(PackagedDraconicBlocks.MARKED_WYVERN_INJECTOR);
	public static final DeferredItem<?> MARKED_DRACONIC_INJECTOR = ITEMS.registerSimpleBlockItem(PackagedDraconicBlocks.MARKED_DRACONIC_INJECTOR);
	public static final DeferredItem<?> MARKED_CHAOTIC_INJECTOR = ITEMS.registerSimpleBlockItem(PackagedDraconicBlocks.MARKED_CHAOTIC_INJECTOR);
}
