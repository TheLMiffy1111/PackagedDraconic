package thelm.packageddraconic.client.event;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import thelm.packageddraconic.block.entity.PackagedDraconicBlockEntities;
import thelm.packageddraconic.client.renderer.FusionCrafterRenderer;
import thelm.packageddraconic.client.renderer.MarkedInjectorRenderer;
import thelm.packageddraconic.client.screen.FusionCrafterScreen;
import thelm.packageddraconic.menu.PackagedDraconicMenus;

public class ClientEventHandler {

	public static final ClientEventHandler INSTANCE = new ClientEventHandler();

	public static ClientEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct(IEventBus modEventBus) {
		modEventBus.register(this);
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		BlockEntityRenderers.register(PackagedDraconicBlockEntities.FUSION_CRAFTER.get(), FusionCrafterRenderer::new);
		BlockEntityRenderers.register(PackagedDraconicBlockEntities.MARKED_INJECTOR.get(), MarkedInjectorRenderer::new);
	}

	@SubscribeEvent
	public void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
		event.register(PackagedDraconicMenus.FUSION_CRAFTER.get(), FusionCrafterScreen::new);
	}
}
