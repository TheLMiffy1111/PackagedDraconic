package thelm.packageddraconic.config;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thelm.packageddraconic.tile.TileFusionCrafter;

public class PackagedDraconicConfig {

	private PackagedDraconicConfig() {}

	public static Configuration config;

	public static void init(File file) {
		MinecraftForge.EVENT_BUS.register(PackagedDraconicConfig.class);
		config = new Configuration(file);
		config.load();
		init();
	}

	public static void init() {
		String category;
		category = "blocks.fusion_crafter";
		TileFusionCrafter.energyCapacity = config.get(category, "energy_capacity", TileFusionCrafter.energyCapacity, "How much FE the Fusion Package Crafter should hold.", 0, Integer.MAX_VALUE).getInt();
		TileFusionCrafter.energyUsage = config.get(category, "energy_usage", TileFusionCrafter.energyUsage, "How much FE/t the Fusion Package Crafter should use.", 0, Integer.MAX_VALUE).getInt();
		TileFusionCrafter.drawMEEnergy = config.get(category, "draw_me_energy", TileFusionCrafter.drawMEEnergy, "Should the Fusion Packager Crafter draw energy from ME systems.").getBoolean();
		if(config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if(event.getModID().equals("packageddraconic")) {
			init();
		}
	}
}
