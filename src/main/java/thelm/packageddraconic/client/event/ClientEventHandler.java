package thelm.packageddraconic.client.event;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thelm.packageddraconic.client.renderer.FusionCrafterRenderer;
import thelm.packageddraconic.client.renderer.MarkedInjectorRenderer;
import thelm.packageddraconic.client.screen.FusionCrafterScreen;
import thelm.packageddraconic.container.FusionCrafterContainer;
import thelm.packageddraconic.tile.FusionCrafterTile;
import thelm.packageddraconic.tile.MarkedInjectorTile;

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
		ScreenManager.register(FusionCrafterContainer.TYPE_INSTANCE, FusionCrafterScreen::new);

		ClientRegistry.bindTileEntityRenderer(FusionCrafterTile.TYPE_INSTANCE, FusionCrafterRenderer::new);
		ClientRegistry.bindTileEntityRenderer(MarkedInjectorTile.TYPE_INSTANCE, MarkedInjectorRenderer::new);
	}
}
