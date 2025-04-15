package thelm.packageddraconic.integration.patchouli.component;

import java.util.List;

import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.lib.RecipeManager;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.gui.GuiBook;

public class ComponentFusionRecipeItemList implements ICustomComponent {

	@VariableHolder
	@SerializedName("output")
	public String outputRaw;
	transient IFusionRecipe fusionRecipe;
	transient int x;
	transient int y;

	@Override
	public void build(int componentX, int componentY, int pageNum) {
		ItemStack output = PatchouliAPI.instance.deserializeItemStack(outputRaw);
		fusionRecipe = RecipeManager.FUSION_REGISTRY.getRecipes().stream().
				filter(recipe->recipe.getRecipeOutput(ItemStack.EMPTY).isItemEqual(output)).
				findFirst().orElse(null);
		x = componentX < 0 ? 50 : componentX;
		y = componentY < 0 ? 43 : componentY;
	}

	@Override
	public void render(IComponentRenderContext context, float partialTicks, int mouseX, int mouseY) {
		if(fusionRecipe != null) {
			List<Ingredient> ingredients = Lists.transform(fusionRecipe.getRecipeIngredients(), CraftingHelper::getIngredient);
			float degreePerInput = 360F/ingredients.size();
			int ticksElapsed = ((GuiBook)context.getGui()).ticksInBook;
			float currentDegree = (GuiScreen.isShiftKeyDown() ? ticksElapsed : ticksElapsed+partialTicks) - 90;
			for(Ingredient ingredient : ingredients) {
				double radians = Math.toRadians(currentDegree);
				double xPos = x + Math.cos(radians)*32;
				double yPos = y + Math.sin(radians)*32;
				GlStateManager.pushMatrix();
				GlStateManager.translate(xPos-MathHelper.floor(xPos), yPos-MathHelper.floor(yPos), 0);
				context.renderIngredient(MathHelper.floor(xPos), MathHelper.floor(yPos), mouseX, mouseY, ingredient);
				GlStateManager.popMatrix();
				currentDegree += degreePerInput;
			}
		}
	}
}
