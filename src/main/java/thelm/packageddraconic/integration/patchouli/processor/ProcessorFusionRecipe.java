package thelm.packageddraconic.integration.patchouli.processor;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.lib.RecipeManager;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorFusionRecipe implements IComponentProcessor {

	public static final NumberFormat ENERGY_FORMAT = new DecimalFormat("#,##0");

	IFusionRecipe fusionRecipe;

	@Override
	public void setup(IVariableProvider<String> variables) {
		ItemStack output = PatchouliAPI.instance.deserializeItemStack(variables.get("output"));
		fusionRecipe = RecipeManager.FUSION_REGISTRY.getRecipes().stream().
				filter(recipe->recipe.getRecipeOutput(ItemStack.EMPTY).isItemEqual(output)).
				findFirst().orElse(null);
	}

	@Override
	public String process(String key) {
		if(fusionRecipe != null) {
			if(key.equals("catalyst")) {
				return PatchouliAPI.instance.serializeItemStack(fusionRecipe.getRecipeCatalyst());
			}
			if(key.equals("tier")) {
				return I18n.translateToLocal("gui.jeiFusion.tier." + fusionRecipe.getRecipeTier());
			}
			if(key.equals("tier_color")) {
				int tier = fusionRecipe.getRecipeTier();
				return tier == 0 ? "5050FF" : tier == 1 ? "8000FF" : tier == 2 ? "FF6600" : "505050";
			}
			if(key.equals("energy")) {
				return ENERGY_FORMAT.format(fusionRecipe.getIngredientEnergyCost() * fusionRecipe.getRecipeIngredients().size()) + " RF";
			}
		}
		return null;
	}
}
