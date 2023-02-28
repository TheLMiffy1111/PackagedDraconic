package thelm.packageddraconic.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.lib.RecipeManager;
import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IngredientNBT;
import net.minecraftforge.common.util.RecipeMatcher;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.api.IRecipeType;
import thelm.packagedauto.api.MiscUtil;
import thelm.packagedauto.util.PatternHelper;

public class RecipeInfoFusion implements IRecipeInfoFusion {

	IFusionRecipe recipe;
	ItemStack inputCore = ItemStack.EMPTY;
	List<ItemStack> inputInjector = new ArrayList<>();
	List<ItemStack> input = new ArrayList<>();
	ItemStack output;
	List<IPackagePattern> patterns = new ArrayList<>();

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		inputInjector.clear();
		input.clear();
		output = ItemStack.EMPTY;
		inputCore = new ItemStack(nbt.getCompoundTag("InputCore"));
		MiscUtil.loadAllItems(nbt.getTagList("InputInjector", 10), inputInjector);
		patterns.clear();
		if(inputInjector.isEmpty()) {
			return;
		}
		for(IFusionRecipe recipe : RecipeManager.FUSION_REGISTRY.getRecipes()) {
			if(recipe.isRecipeCatalyst(inputCore) && recipe.getRecipeIngredients().size() == inputInjector.size()) {
				List<Ingredient> matchers = Lists.transform(recipe.getRecipeIngredients(), RecipeInfoFusion::getIngredient);
				if(RecipeMatcher.findMatches(inputInjector, matchers) != null) {
					this.recipe = recipe;
					if(!recipe.getRecipeCatalyst().isEmpty()) {
						inputCore.setCount(recipe.getRecipeCatalyst().getCount());
					}
					break;
				}
			}
		}
		if(recipe != null) {
			List<ItemStack> toCondense = new ArrayList<>(inputInjector);
			toCondense.add(inputCore);
			input.addAll(MiscUtil.condenseStacks(toCondense));
			output = recipe.getRecipeOutput(inputCore);
			for(int i = 0; i*9 < input.size(); ++i) {
				patterns.add(new PatternHelper(this, i));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound inputCoreTag = inputCore.writeToNBT(new NBTTagCompound());
		NBTTagList inputInjectorTag = MiscUtil.saveAllItems(new NBTTagList(), inputInjector);
		nbt.setTag("InputCore", inputCoreTag);
		nbt.setTag("InputInjector", inputInjectorTag);
		return nbt;
	}

	@Override
	public IRecipeType getRecipeType() {
		return RecipeTypeFusion.INSTANCE;
	}

	@Override
	public boolean isValid() {
		return recipe != null;
	}

	@Override
	public List<IPackagePattern> getPatterns() {
		return Collections.unmodifiableList(patterns);
	}

	@Override
	public ItemStack getCoreInput() {
		return inputCore.copy();
	}

	@Override
	public List<ItemStack> getInjectorInputs() {
		return Collections.unmodifiableList(inputInjector);
	}

	@Override
	public List<ItemStack> getInputs() {
		return Collections.unmodifiableList(input);
	}

	@Override
	public ItemStack getOutput() {
		return output.copy();
	}

	@Override
	public long getEnergyRequired() {
		return recipe.getIngredientEnergyCost();
	}

	@Override
	public IFusionRecipe getRecipe() {
		return recipe;
	}

	@Override
	public void generateFromStacks(List<ItemStack> input, List<ItemStack> output, World world) {
		recipe = null;
		inputCore = ItemStack.EMPTY;
		inputInjector.clear();
		this.input.clear();
		patterns.clear();
		int[] slotArray = RecipeTypeFusion.SLOTS.toIntArray();
		ArrayUtils.shift(slotArray, 0, 25, 1);
		for(int i = 0; i < 49; ++i) {
			ItemStack toSet = input.get(slotArray[i]);
			if(!toSet.isEmpty()) {
				toSet.setCount(1);
				if(i == 0) {
					inputCore = toSet;
				}
				else {
					inputInjector.add(toSet.copy());
				}
			}
			else if(i == 0) {
				return;
			}
		}
		for(IFusionRecipe recipe : RecipeManager.FUSION_REGISTRY.getRecipes()) {
			if(recipe.isRecipeCatalyst(inputCore) && recipe.getRecipeIngredients().size() == inputInjector.size()) {
				List<Ingredient> matchers = Lists.transform(recipe.getRecipeIngredients(), RecipeInfoFusion::getIngredient);
				if(RecipeMatcher.findMatches(inputInjector, matchers) != null) {
					this.recipe = recipe;
					if(!recipe.getRecipeCatalyst().isEmpty()) {
						inputCore.setCount(recipe.getRecipeCatalyst().getCount());
						inputCore = inputCore.copy();
					}
					List<ItemStack> toCondense = new ArrayList<>(inputInjector);
					toCondense.add(inputCore);
					this.input.addAll(MiscUtil.condenseStacks(toCondense));
					this.output = recipe.getRecipeOutput(inputCore).copy();
					for(int i = 0; i*9 < this.input.size(); ++i) {
						patterns.add(new PatternHelper(this, i));
					}
					return;
				}
			}
		}
	}

	protected static Ingredient getIngredient(Object obj) {
		if(obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			return stack.hasTagCompound() ? new IngredientNBT(stack) {} : Ingredient.fromStacks(stack);
		}
		else {
			return CraftingHelper.getIngredient(obj);
		}
	}

	@Override
	public Int2ObjectMap<ItemStack> getEncoderStacks() {
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		int[] slotArray = RecipeTypeFusion.SLOTS.toIntArray();
		ArrayUtils.remove(slotArray, 24);
		map.put(40, inputCore);
		for(int i = 0; i < inputInjector.size(); ++i) {
			map.put(slotArray[i], inputInjector.get(i));
		}
		return map;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RecipeInfoFusion) {
			RecipeInfoFusion other = (RecipeInfoFusion)obj;
			for(int i = 0; i < input.size(); ++i) {
				if(!ItemStack.areItemStacksEqualUsingNBTShareTag(input.get(i), other.input.get(i))) {
					return false;
				}
			}
			return recipe.equals(other.recipe);
		}
		return false;
	}

	@Override
	public int hashCode() {
		Object[] toHash = new Object[2];
		Object[] inputArray = new Object[input.size()];
		for(int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.get(i);
			inputArray[i] = new Object[] {stack.getItem(), stack.getItemDamage(), stack.getCount(), stack.getTagCompound()};
		}
		toHash[0] = recipe;
		toHash[1] = inputArray;
		return Arrays.deepHashCode(toHash);
	}
}
