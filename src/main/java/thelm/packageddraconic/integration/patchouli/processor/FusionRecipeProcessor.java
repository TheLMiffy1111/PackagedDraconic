package thelm.packageddraconic.integration.patchouli.processor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import thelm.packagedauto.util.MiscHelper;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class FusionRecipeProcessor implements IComponentProcessor {

	public static final NumberFormat ENERGY_FORMAT = new DecimalFormat("#,##0");

	IFusionRecipe fusionRecipe;

	@Override
	public void setup(Level level, IVariableProvider variables) {
		ResourceLocation recipeId = ResourceLocation.parse(variables.get("recipe", level.registryAccess()).asString());
		Recipe<?> recipe = MiscHelper.INSTANCE.getRecipeManager().byKey(recipeId).map(RecipeHolder::value).orElse(null);
		if(recipe instanceof IFusionRecipe fusionRecipe) {
			this.fusionRecipe = fusionRecipe;
		}
	}

	@Override
	public IVariable process(Level level, String key) {
		if(fusionRecipe != null) {
			if(key.equals("catalyst")) {
				return IVariable.from(fusionRecipe.getCatalyst(), level.registryAccess());
			}
			if(key.equals("output")) {
				return IVariable.from(fusionRecipe.getResultItem(level.registryAccess()), level.registryAccess());
			}
			if(key.equals("tier")) {
				return IVariable.from(Component.translatable("gui.draconicevolution.fusion_craft.tier." + fusionRecipe.getRecipeTier().name().toLowerCase(Locale.US)), level.registryAccess());
			}
			if(key.equals("tier_color")) {
				int tier = fusionRecipe.getRecipeTier().index;
				return IVariable.wrap(tier == 0 ? "5050FF" : tier == 1 ? "8000FF" : tier == 2 ? "FF6600" : "505050", level.registryAccess());
			}
			if(key.equals("energy")) {
				return IVariable.wrap(ENERGY_FORMAT.format(fusionRecipe.getEnergyCost()) + " OP", level.registryAccess());
			}
		}
		return null;
	}
}
