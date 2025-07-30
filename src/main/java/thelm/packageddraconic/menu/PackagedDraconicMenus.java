package thelm.packageddraconic.menu;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thelm.packagedauto.menu.factory.PositionalBlockEntityMenuFactory;

public class PackagedDraconicMenus {

	private PackagedDraconicMenus() {}

	public static <C extends AbstractContainerMenu, T extends BlockEntity> Supplier<MenuType<C>> of(PositionalBlockEntityMenuFactory.Factory<C, T> factory) {
		return ()->IMenuTypeExtension.create(new PositionalBlockEntityMenuFactory<>(factory));
	}

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "packageddraconic");

	public static final DeferredHolder<MenuType<?>, MenuType<FusionCrafterMenu>> FUSION_CRAFTER = MENUS.register("fusion_crafter", of(FusionCrafterMenu::new));
}
