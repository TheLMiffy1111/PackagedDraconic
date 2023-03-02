package thelm.packageddraconic.inventory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;
import com.brandon3055.draconicevolution.api.crafting.IFusionInventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class FakeFusionInventory implements IFusionInventory {

	private ItemStack catalystStack = ItemStack.EMPTY;
	private NonNullList<ItemStack> injectorStacks = NonNullList.create();

	@Override
	public ItemStack getCatalystStack() {
		return catalystStack;
	}

	@Override
	public ItemStack getOutputStack() {
		return ItemStack.EMPTY;
	}

	@Override
	public void setCatalystStack(ItemStack stack) {
		catalystStack = stack;
	}

	@Override
	public void setOutputStack(ItemStack stack) {}

	public List<ItemStack> getInjectorStacks() {
		return Collections.unmodifiableList(injectorStacks);
	}

	public void setInjectorStacks(List<ItemStack> injectorStacks) {
		this.injectorStacks.clear();
		injectorStacks.stream().filter(s->s != null && !s.isEmpty()).forEach(this.injectorStacks::add);
	}

	@Override
	public List<IFusionInjector> getInjectors() {
		return injectorStacks.stream().map(FakeFusionInjector::new).collect(Collectors.toList());
	}

	@Override
	public TechLevel getMinimumTier() {
		return TechLevel.CHAOTIC;
	}

	public static record FakeFusionInjector(ItemStack stack) implements IFusionInjector {

		@Override
		public TechLevel getInjectorTier() {
			return TechLevel.CHAOTIC;
		}

		@Override
		public ItemStack getInjectorStack() {
			return stack;
		}

		@Override
		public void setInjectorStack(ItemStack stack) {}

		@Override
		public long getInjectorEnergy() {
			return 0;
		}

		@Override
		public void setInjectorEnergy(long energy) {}

		@Override
		public void setEnergyRequirement(long maxEnergy, long chargeRate) {}

		@Override
		public long getEnergyRequirement() {
			return 0;
		}

		@Override
		public boolean validate() {
			return false;
		}
	}
}
