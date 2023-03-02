package thelm.packageddraconic.recipe;

import java.util.List;

import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;

import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.api.IPackageRecipeInfo;

public interface IFusionPackageRecipeInfo extends IPackageRecipeInfo {

	ItemStack getCoreInput();

	List<ItemStack> getInjectorInputs();

	ItemStack getOutput();

	long getEnergyRequired();

	IFusionRecipe getRecipe();

	@Override
	default List<ItemStack> getOutputs() {
		return List.of(getOutput());
	}
}
