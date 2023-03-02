package thelm.packageddraconic.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;

public class PackagedDraconicConfig {

	private PackagedDraconicConfig() {}

	private static ForgeConfigSpec serverSpec;

	public static ForgeConfigSpec.IntValue fusionCrafterEnergyCapacity;
	public static ForgeConfigSpec.IntValue fusionCrafterEnergyUsage;
	public static ForgeConfigSpec.BooleanValue fusionCrafterDrawMEEnergy;

	public static void registerConfig() {
		buildConfig();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
	}

	private static void buildConfig() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.push("fusion_crafter");
		builder.comment("How much FE the Fusion Package Crafter should hold.");
		fusionCrafterEnergyCapacity = builder.defineInRange("energy_capacity", 5000, 0, Integer.MAX_VALUE);
		builder.comment("How much FE/t the Fusion Package Crafter should use.");
		fusionCrafterEnergyUsage = builder.defineInRange("energy_usage", 5, 0, Integer.MAX_VALUE);
		builder.comment("Should the Fusion Package Crafter draw energy from ME systems.");
		fusionCrafterDrawMEEnergy = builder.define("draw_me_energy", true);
		builder.pop();

		serverSpec = builder.build();
	}

	public static void reloadServerConfig() {
		FusionCrafterBlockEntity.energyCapacity = fusionCrafterEnergyCapacity.get();
		FusionCrafterBlockEntity.energyUsage = fusionCrafterEnergyUsage.get();
		FusionCrafterBlockEntity.drawMEEnergy = fusionCrafterDrawMEEnergy.get();
	}
}
