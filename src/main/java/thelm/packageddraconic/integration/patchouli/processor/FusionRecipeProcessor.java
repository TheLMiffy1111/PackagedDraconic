package thelm.packageddraconic.integration.patchouli.processor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import thelm.packagedauto.util.MiscHelper;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class FusionRecipeProcessor implements IComponentProcessor {

	public static final NumberFormat ENERGY_FORMAT = new DecimalFormat("#,##0");

	IFusionRecipe fusionRecipe;

	@Override
	public void setup(IVariableProvider variables) {
		ResourceLocation recipeId = new ResourceLocation(variables.get("recipe").asString());
		IRecipe<?> recipe = MiscHelper.INSTANCE.getRecipeManager().byKey(recipeId).orElse(null);
		if(recipe instanceof IFusionRecipe) {
			this.fusionRecipe = (IFusionRecipe)recipe;
		}
	}

	@Override
	public IVariable process(String key) {
		if(fusionRecipe != null) {
			if(key.equals("catalyst")) {
				return IVariable.from(fusionRecipe.getCatalyst());
			}
			if(key.equals("output")) {
				return IVariable.from(fusionRecipe.getResultItem());
			}
			if(key.equals("tier")) {
				return IVariable.from(new TranslationTextComponent("gui.draconicevolution.fusion_craft.tier." + fusionRecipe.getRecipeTier().name().toLowerCase(Locale.US)));
			}
			if(key.equals("tier_color")) {
				int tier = fusionRecipe.getRecipeTier().index;
				return IVariable.wrap(tier == 0 ? "5050FF" : tier == 1 ? "8000FF" : tier == 2 ? "FF6600" : "505050");
			}
			if(key.equals("energy")) {
				return IVariable.wrap(ENERGY_FORMAT.format(fusionRecipe.getEnergyCost()) + " OP");
			}
		}
		return null;
	}
}
