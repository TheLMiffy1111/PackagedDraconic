package thelm.packageddraconic.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IngredientStack;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.api.IPackageRecipeType;
import thelm.packagedauto.util.MiscHelper;
import thelm.packagedauto.util.PackagePattern;
import thelm.packageddraconic.inventory.FakeFusionInventory;

public class FusionPackageRecipeInfo implements IFusionPackageRecipeInfo {

	IFusionRecipe recipe;
	ItemStack inputCore = ItemStack.EMPTY;
	List<ItemStack> inputInjector = new ArrayList<>();
	List<ItemStack> input = new ArrayList<>();
	ItemStack output;
	List<IPackagePattern> patterns = new ArrayList<>();

	@Override
	public void load(CompoundTag nbt) {
		inputInjector.clear();
		input.clear();
		output = ItemStack.EMPTY;
		inputCore = ItemStack.of(nbt.getCompound("InputCore"));
		MiscHelper.INSTANCE.loadAllItems(nbt.getList("InputInjector", 10), inputInjector);
		patterns.clear();
		Recipe recipe = MiscHelper.INSTANCE.getRecipeManager().byKey(new ResourceLocation(nbt.getString("Recipe"))).orElse(null);
		if(inputInjector.isEmpty()) {
			return;
		}
		if(recipe instanceof IFusionRecipe) {
			this.recipe = (IFusionRecipe)recipe;
			if(this.recipe.getCatalyst() instanceof IngredientStack ingStack) {
				inputCore.setCount(ingStack.getCount());
			}
			List<ItemStack> toCondense = new ArrayList<>(inputInjector);
			toCondense.add(inputCore);
			input.addAll(MiscHelper.INSTANCE.condenseStacks(toCondense));
			FakeFusionInventory matrix = new FakeFusionInventory();
			matrix.setCatalystStack(inputCore);
			matrix.setInjectorStacks(inputInjector);
			output = this.recipe.assemble(matrix).copy();
			for(int i = 0; i*9 < input.size(); ++i) {
				patterns.add(new PackagePattern(this, i));
			}
		}
	}

	@Override
	public void save(CompoundTag nbt) {
		if(recipe != null) {
			nbt.putString("Recipe", recipe.getId().toString());
		}
		CompoundTag inputCoreTag = inputCore.save(new CompoundTag());
		ListTag inputInjectorTag = MiscHelper.INSTANCE.saveAllItems(new ListTag(), inputInjector);
		nbt.put("InputCore", inputCoreTag);
		nbt.put("InputInjector", inputInjectorTag);
	}

	@Override
	public IPackageRecipeType getRecipeType() {
		return FusionPackageRecipeType.INSTANCE;
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
		return recipe.getEnergyCost();
	}

	@Override
	public IFusionRecipe getRecipe() {
		return recipe;
	}

	@Override
	public void generateFromStacks(List<ItemStack> input, List<ItemStack> output, Level level) {
		recipe = null;
		inputCore = ItemStack.EMPTY;
		inputInjector.clear();
		this.input.clear();
		patterns.clear();
		int[] slotArray = FusionPackageRecipeType.SLOTS.toIntArray();
		ArrayUtils.shift(slotArray, 0, 25, 1);
		for(int i = 0; i < 49; ++i) {
			ItemStack toSet = input.get(slotArray[i]);
			if(!toSet.isEmpty()) {
				toSet.setCount(1);
				if(i == 0) {
					toSet.setCount(toSet.getMaxStackSize());
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
		FakeFusionInventory matrix = new FakeFusionInventory();
		matrix.setCatalystStack(inputCore);
		matrix.setInjectorStacks(inputInjector);
		IFusionRecipe recipe = MiscHelper.INSTANCE.getRecipeManager().getRecipeFor(DraconicAPI.FUSION_RECIPE_TYPE, matrix, level).orElse(null);
		if(recipe != null) {
			this.recipe = recipe;
			if(recipe.getCatalyst() instanceof IngredientStack ingStack) {
				inputCore.setCount(ingStack.getCount());
				inputCore = inputCore.copy();
			}
			List<ItemStack> toCondense = new ArrayList<>(inputInjector);
			toCondense.add(inputCore);
			this.input.addAll(MiscHelper.INSTANCE.condenseStacks(toCondense));
			this.output = recipe.assemble(matrix).copy();
			for(int i = 0; i*9 < this.input.size(); ++i) {
				patterns.add(new PackagePattern(this, i));
			}
			return;
		}
		inputCore.setCount(1);
	}

	@Override
	public Int2ObjectMap<ItemStack> getEncoderStacks() {
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		int[] slotArray = FusionPackageRecipeType.SLOTS.toIntArray();
		ArrayUtils.remove(slotArray, 24);
		map.put(40, inputCore);
		for(int i = 0; i < inputInjector.size(); ++i) {
			map.put(slotArray[i], inputInjector.get(i));
		}
		return map;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FusionPackageRecipeInfo) {
			FusionPackageRecipeInfo other = (FusionPackageRecipeInfo)obj;
			if(input.size() != other.input.size()) {
				return false;
			}
			for(int i = 0; i < input.size(); ++i) {
				if(!ItemStack.matches(input.get(i), other.input.get(i))) {
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
			inputArray[i] = new Object[] {stack.getItem(), stack.getCount(), stack.getTag()};
		}
		toHash[0] = recipe;
		toHash[1] = inputArray;
		return Arrays.deepHashCode(toHash);
	}
}
