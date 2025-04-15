package thelm.packageddraconic.integration.patchouli.component;

import java.util.List;
import java.util.function.UnaryOperator;

import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.google.gson.annotations.SerializedName;

import codechicken.lib.math.MathHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import thelm.packagedauto.util.MiscHelper;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

public class FusionRecipeItemListComponent implements ICustomComponent {

	@SerializedName("recipe")
	public IVariable recipeRaw;
	transient IFusionRecipe fusionRecipe;
	transient int x;
	transient int y;

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		ResourceLocation recipeId = new ResourceLocation(lookup.apply(recipeRaw).asString());
		Recipe<?> recipe = MiscHelper.INSTANCE.getRecipeManager().byKey(recipeId).orElse(null);
		if(recipe instanceof IFusionRecipe fusionRecipe) {
			this.fusionRecipe = fusionRecipe;
		}
	}

	@Override
	public void build(int componentX, int componentY, int pageNum) {
		x = componentX < 0 ? 50 : componentX;
		y = componentY < 0 ? 43 : componentY;
	}

	@Override
	public void render(GuiGraphics guiGraphics, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY) {
		if(fusionRecipe != null) {
			List<Ingredient> ingredients = fusionRecipe.getIngredients();
			float degreePerInput = 360F/ingredients.size();
			int ticksElapsed = context.getTicksInBook();
			float currentDegree = (Screen.hasShiftDown() ? ticksElapsed : ticksElapsed+partialTicks) - 90;
			for(Ingredient ingredient : ingredients) {
				double radians = Math.toRadians(currentDegree);
				double xPos = x + Math.cos(radians)*32;
				double yPos = y + Math.sin(radians)*32;
				guiGraphics.pose().pushPose();
				guiGraphics.pose().translate(xPos-MathHelper.floor(xPos), yPos-MathHelper.floor(yPos), 0);
				context.renderIngredient(guiGraphics, MathHelper.floor(xPos), MathHelper.floor(yPos), mouseX, mouseY, ingredient);
				guiGraphics.pose().popPose();
				currentDegree += degreePerInput;
			}
		}
	}
}
