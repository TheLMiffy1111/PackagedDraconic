package thelm.packageddraconic.block.entity;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thelm.packagedauto.util.MiscHelper;
import thelm.packageddraconic.block.PackagedDraconicBlocks;
import thelm.packageddraconic.integration.appeng.blockentity.AEFusionCrafterBlockEntity;
import thelm.packageddraconic.integration.appeng.blockentity.AEMarkedInjectorBlockEntity;

public class PackagedDraconicBlockEntities {

	private PackagedDraconicBlockEntities() {}

	public static <T extends BlockEntity> Supplier<BlockEntityType<T>> of(BooleanSupplier condition, Supplier<Supplier<BlockEntityType.BlockEntitySupplier<? extends T>>> trueSupplier, Supplier<Supplier<BlockEntityType.BlockEntitySupplier<? extends T>>> falseSupplier, Supplier<Block>... validBlocks) {
		return ()->new BlockEntityType<>(MiscHelper.INSTANCE.conditionalSupplier(condition, trueSupplier, falseSupplier).get(), Arrays.stream(validBlocks).map(Supplier::get).filter(Objects::nonNull).collect(Collectors.toSet()), null);
	}

	public static final BooleanSupplier AE2_LOADED = ()->ModList.get().isLoaded("ae2");

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "packageddraconic");
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FusionCrafterBlockEntity>> FUSION_CRAFTER = BLOCK_ENTITIES.register(
			"fusion_crafter", of(AE2_LOADED, ()->()->AEFusionCrafterBlockEntity::new, ()->()->FusionCrafterBlockEntity::new, PackagedDraconicBlocks.FUSION_CRAFTER));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MarkedInjectorBlockEntity>> MARKED_INJECTOR = BLOCK_ENTITIES.register(
			"marked_injector", of(AE2_LOADED, ()->()->AEMarkedInjectorBlockEntity::new, ()->()->MarkedInjectorBlockEntity::new,
					PackagedDraconicBlocks.MARKED_DRACONIUM_INJECTOR, PackagedDraconicBlocks.MARKED_WYVERN_INJECTOR,
					PackagedDraconicBlocks.MARKED_DRACONIC_INJECTOR, PackagedDraconicBlocks.MARKED_CHAOTIC_INJECTOR));
}
