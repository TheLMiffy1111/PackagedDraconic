package thelm.packageddraconic.integration.appeng;

import appeng.api.AECapabilities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import thelm.packagedauto.integration.appeng.AppEngUtil;
import thelm.packageddraconic.block.entity.PackagedDraconicBlockEntities;

public class AppEngEventHandler {

	public static final AppEngEventHandler INSTANCE = new AppEngEventHandler();

	public static AppEngEventHandler getInstance() {
		return INSTANCE;
	}

	@SubscribeEvent
	public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, PackagedDraconicBlockEntities.FUSION_CRAFTER.get(), (be, v)->AppEngUtil.getAsInWorldGridNodeHost(be));
		event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, PackagedDraconicBlockEntities.MARKED_INJECTOR.get(), (be, v)->AppEngUtil.getAsInWorldGridNodeHost(be));
	}
}
