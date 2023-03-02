package thelm.packageddraconic.client.event;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;
import thelm.packageddraconic.block.entity.MarkedInjectorBlockEntity;
import thelm.packageddraconic.client.renderer.FusionCrafterRenderer;
import thelm.packageddraconic.client.renderer.MarkedInjectorRenderer;
import thelm.packageddraconic.client.screen.FusionCrafterScreen;
import thelm.packageddraconic.menu.FusionCrafterMenu;

public class ClientEventHandler {

	public static final ClientEventHandler INSTANCE = new ClientEventHandler();

	public static ClientEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		MenuScreens.register(FusionCrafterMenu.TYPE_INSTANCE, FusionCrafterScreen::new);

		BlockEntityRenderers.register(FusionCrafterBlockEntity.TYPE_INSTANCE, FusionCrafterRenderer::new);
		BlockEntityRenderers.register(MarkedInjectorBlockEntity.TYPE_INSTANCE, MarkedInjectorRenderer::new);
	}
}
