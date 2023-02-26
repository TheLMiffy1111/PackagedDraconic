package thelm.packageddraconic.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import thelm.packageddraconic.config.PackagedDraconicConfig;

public class GuiPackagedDraconicConfig extends GuiConfig {

	public GuiPackagedDraconicConfig(GuiScreen parent) {
		super(parent, getConfigElements(), "packagedDraconic", false, false, getAbridgedConfigPath(PackagedDraconicConfig.config.toString()));
	}

	private static List<IConfigElement> getConfigElements() {
		ArrayList<IConfigElement> list = new ArrayList<>();
		for(String category : PackagedDraconicConfig.config.getCategoryNames()) {
			list.add(new ConfigElement(PackagedDraconicConfig.config.getCategory(category)));
		}
		return list;
	}
}
