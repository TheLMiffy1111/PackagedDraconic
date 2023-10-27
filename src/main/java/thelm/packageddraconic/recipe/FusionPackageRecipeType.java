package thelm.packageddraconic.recipe;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.brandon3055.draconicevolution.init.DEContent;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.api.IPackageRecipeInfo;
import thelm.packagedauto.api.IPackageRecipeType;
import thelm.packagedauto.api.IRecipeSlotViewWrapper;
import thelm.packagedauto.api.IRecipeSlotsViewWrapper;

public class FusionPackageRecipeType implements IPackageRecipeType {

	public static final FusionPackageRecipeType INSTANCE = new FusionPackageRecipeType();
	public static final ResourceLocation NAME = new ResourceLocation("packageddraconic:fusion");
	public static final IntSet SLOTS;
	public static final List<ResourceLocation> CATEGORIES = List.of(new ResourceLocation("draconicevolution:fusion_crafting"));
	public static final Vec3i COLOR = new Vec3i(139, 139, 139);
	public static final Vec3i COLOR_CENTER = new Vec3i(179, 139, 179);
	public static final Vec3i COLOR_DISABLED = new Vec3i(64, 64, 64);

	static {
		SLOTS = new IntRBTreeSet();
		for(int i = 1; i < 8; ++i) {
			for(int j = 1; j < 8; ++j) {
				SLOTS.add(9*i+j);
			}
		}
	}

	@Override
	public ResourceLocation getName() {
		return NAME;
	}

	@Override
	public MutableComponent getDisplayName() {
		return new TranslatableComponent("recipe.packageddraconic.fusion");
	}

	@Override
	public MutableComponent getShortDisplayName() {
		return new TranslatableComponent("recipe.packageddraconic.fusion.short");
	}

	@Override
	public IPackageRecipeInfo getNewRecipeInfo() {
		return new FusionPackageRecipeInfo();
	}

	@Override
	public IntSet getEnabledSlots() {
		return SLOTS;
	}

	@Override
	public boolean hasContainerItem() {
		return false;
	}

	@Override
	public List<ResourceLocation> getJEICategories() {
		return CATEGORIES;
	}

	@Override
	public Int2ObjectMap<ItemStack> getRecipeTransferMap(IRecipeSlotsViewWrapper recipeLayoutWrapper) {
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		List<IRecipeSlotViewWrapper> slotViews = recipeLayoutWrapper.getRecipeSlotViews();
		int index = 0;
		int[] slotArray = SLOTS.toIntArray();
		ArrayUtils.shift(slotArray, 0, 25, 1);
		for(IRecipeSlotViewWrapper slotView : slotViews) {
			if(slotView.isInput()) {
				Object displayed = slotView.getDisplayedIngredient().orElse(null);
				if(displayed instanceof ItemStack stack && !stack.isEmpty()) {
					map.put(slotArray[index], stack);
				}
				++index;
			}
			if(index >= 49) {
				break;
			}
		}
		return map;
	}

	@Override
	public Object getRepresentation() {
		return new ItemStack(DEContent.crafting_core);
	}

	@Override
	public Vec3i getSlotColor(int slot) {
		if(!SLOTS.contains(slot) && slot != 81) {
			return COLOR_DISABLED;
		}
		else if(slot == 40) {
			return COLOR_CENTER;
		}
		return COLOR;
	}
}
