package thelm.packageddraconic.recipe;

import java.util.Collections;
import java.util.List;

import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;

import net.minecraft.item.ItemStack;
import thelm.packagedauto.api.IRecipeInfo;

public interface IRecipeInfoFusion extends IRecipeInfo {

	ItemStack getCoreInput();

	List<ItemStack> getInjectorInputs();

	ItemStack getOutput();

	long getEnergyRequired();

	IFusionRecipe getRecipe();

	@Override
	default List<ItemStack> getOutputs() {
		return Collections.singletonList(getOutput());
	}
}
