package thelm.packageddraconic.creativetab;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thelm.packageddraconic.item.PackagedDraconicItems;

public class PackagedDraconicCreativeTabs {

	private PackagedDraconicCreativeTabs() {}

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "packageddraconic");

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_TABS.register(
			"tab", ()->CreativeModeTab.builder().
			title(Component.translatable("itemGroup.packageddraconic")).
			icon(PackagedDraconicItems.FUSION_CRAFTER::toStack).
			displayItems((parameters, output)->{
				output.accept(PackagedDraconicItems.FUSION_CRAFTER);
				output.accept(PackagedDraconicItems.MARKED_DRACONIUM_INJECTOR);
				output.accept(PackagedDraconicItems.MARKED_WYVERN_INJECTOR);
				output.accept(PackagedDraconicItems.MARKED_DRACONIC_INJECTOR);
				output.accept(PackagedDraconicItems.MARKED_CHAOTIC_INJECTOR);
			}).build());
}
