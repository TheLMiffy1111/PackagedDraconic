package thelm.packageddraconic.recipe;

import java.util.List;

import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import thelm.packagedauto.api.IPackageRecipeInfo;

public interface IFusionPackageRecipeInfo extends IPackageRecipeInfo {

	ItemStack getCoreInput();

	List<ItemStack> getInjectorInputs();

	ItemStack getOutput();

	int getTierRequired();

	long getEnergyRequired();

	IFusionRecipe getRecipe();

	ResourceLocation getRecipeId();

	default RecipeHolder<IFusionRecipe> getRecipeHolder() {
		return new RecipeHolder<>(getRecipeId(), getRecipe());
	}

	@Override
	default List<ItemStack> getOutputs() {
		ItemStack output = getOutput();
		return output.isEmpty() ? List.of() : List.of(output);
	}
}
