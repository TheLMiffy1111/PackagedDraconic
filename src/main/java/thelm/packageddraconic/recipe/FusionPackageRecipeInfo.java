package thelm.packageddraconic.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.StackIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.api.IPackageRecipeType;
import thelm.packagedauto.util.MiscHelper;
import thelm.packagedauto.util.PackagePattern;
import thelm.packageddraconic.inventory.FakeFusionInventory;

public class FusionPackageRecipeInfo implements IFusionPackageRecipeInfo {

	public static final MapCodec<FusionPackageRecipeInfo> MAP_CODEC = RecordCodecBuilder.mapCodec(instance->instance.group(
			ResourceLocation.CODEC.fieldOf("id").forGetter(FusionPackageRecipeInfo::getRecipeId),
			ItemStack.OPTIONAL_CODEC.orElse(ItemStack.EMPTY).fieldOf("core").forGetter(FusionPackageRecipeInfo::getCoreInput),
			ItemStack.OPTIONAL_CODEC.orElse(ItemStack.EMPTY).sizeLimitedListOf(54).fieldOf("injector").forGetter(FusionPackageRecipeInfo::getInjectorInputs)).
			apply(instance, FusionPackageRecipeInfo::new));
	public static final Codec<FusionPackageRecipeInfo> CODEC = MAP_CODEC.codec();
	public static final StreamCodec<RegistryFriendlyByteBuf, FusionPackageRecipeInfo> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, FusionPackageRecipeInfo::getRecipeId,
			ItemStack.OPTIONAL_STREAM_CODEC, FusionPackageRecipeInfo::getCoreInput,
			ItemStack.OPTIONAL_LIST_STREAM_CODEC, FusionPackageRecipeInfo::getInjectorInputs,
			FusionPackageRecipeInfo::new);

	private final ResourceLocation id;
	private final IFusionRecipe recipe;
	private final ItemStack inputCore;
	private final List<ItemStack> inputInjector;
	private final List<ItemStack> input;
	private final ItemStack output;
	private final List<IPackagePattern> patterns = new ArrayList<>();

	public FusionPackageRecipeInfo(ResourceLocation id, ItemStack inputCore, List<ItemStack> inputInjector) {
		this.id = id;
		this.inputCore = inputCore;
		this.inputInjector = inputInjector;
		List<ItemStack> matrixList = new ArrayList<>(inputInjector.size()+1);
		matrixList.add(inputCore);
		matrixList.addAll(inputInjector);
		Recipe<?> recipeSer = MiscHelper.INSTANCE.getRecipeManager().byKey(id).map(RecipeHolder::value).orElse(null);
		if(recipeSer instanceof IFusionRecipe fusionRecipe) {
			recipe = fusionRecipe;
			if(recipe.getCatalyst().getCustomIngredient() instanceof StackIngredient ingStack) {
				inputCore.setCount(ingStack.getCount());
			}
			else {
				inputCore.setCount(1);
			}
			FakeFusionInventory matrix = new FakeFusionInventory();
			matrix.setCatalystStack(inputCore);
			matrix.setInjectorStacks(inputInjector);
			output = recipe.assemble(matrix, MiscHelper.INSTANCE.getRegistryAccess()).copy();
		}
		else {
			recipe = null;
			output = ItemStack.EMPTY;
		}
		input = MiscHelper.INSTANCE.condenseStacks(matrixList);
		for(int i = 0; i*9 < input.size(); ++i) {
			patterns.add(new PackagePattern(this, i));
		}
	}

	public FusionPackageRecipeInfo(List<ItemStack> inputs, Level level) {
		ItemStack inputCore = ItemStack.EMPTY;
		inputInjector = new ArrayList<>();
		int[] slotArray = FusionPackageRecipeType.SLOTS.toIntArray();
		ArrayUtils.shift(slotArray, 0, 28, 1);
		for(int i = 0; i < 55; ++i) {
			ItemStack toSet = inputs.get(slotArray[i]);
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
		}
		this.inputCore = inputCore;
		List<ItemStack> matrixList = new ArrayList<>(inputInjector.size()+1);
		matrixList.add(inputCore);
		matrixList.addAll(inputInjector);
		FakeFusionInventory matrix = new FakeFusionInventory();
		matrix.setCatalystStack(inputCore);
		matrix.setInjectorStacks(inputInjector);
		RecipeHolder<IFusionRecipe> recipeHolder = MiscHelper.INSTANCE.getRecipeManager().getRecipeFor(DraconicAPI.FUSION_RECIPE_TYPE.get(), matrix, level).orElse(null);
		if(recipeHolder != null) {
			id = recipeHolder.id();
			recipe = recipeHolder.value();
			if(recipe.getCatalyst().getCustomIngredient() instanceof StackIngredient ingStack) {
				inputCore.setCount(ingStack.getCount());
			}
			else {
				inputCore.setCount(1);
			}
			output = recipe.assemble(matrix, level.registryAccess()).copy();
		}
		else {
			id = null;
			recipe = null;
			inputCore.setCount(1);
			output = null;
		}
		input = MiscHelper.INSTANCE.condenseStacks(matrixList);
		for(int i = 0; i*9 < input.size(); ++i) {
			this.patterns.add(new PackagePattern(this, i));
		}
	}


	@Override
	public IPackageRecipeType getRecipeType() {
		return FusionPackageRecipeType.INSTANCE;
	}

	@Override
	public boolean isValid() {
		return id != null && recipe != null;
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
	public int getTierRequired() {
		return recipe.getRecipeTier().index;
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
	public ResourceLocation getRecipeId() {
		return id;
	}

	@Override
	public Int2ObjectMap<ItemStack> getEncoderStacks() {
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		int[] slotArray = FusionPackageRecipeType.SLOTS.toIntArray();
		slotArray = ArrayUtils.remove(slotArray, 27);
		map.put(40, inputCore);
		for(int i = 0; i < inputInjector.size(); ++i) {
			map.put(slotArray[i], inputInjector.get(i));
		}
		return map;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FusionPackageRecipeInfo other) {
			return MiscHelper.INSTANCE.recipeEquals(this, recipe, other, other.recipe);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return MiscHelper.INSTANCE.recipeHashCode(this, recipe);
	}
}
