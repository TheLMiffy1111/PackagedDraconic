package thelm.packageddraconic.block;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PackagedDraconicBlocks {

	private PackagedDraconicBlocks() {}

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("packageddraconic");

	public static final DeferredBlock<Block> FUSION_CRAFTER = BLOCKS.register("fusion_crafter", FusionCrafterBlock::new);
	public static final DeferredBlock<Block> MARKED_DRACONIUM_INJECTOR = BLOCKS.register("marked_draconium_injector", ()->new MarkedInjectorBlock(0));
	public static final DeferredBlock<Block> MARKED_WYVERN_INJECTOR = BLOCKS.register("marked_wyvern_injector", ()->new MarkedInjectorBlock(1));
	public static final DeferredBlock<Block> MARKED_DRACONIC_INJECTOR = BLOCKS.register("marked_draconic_injector", ()->new MarkedInjectorBlock(2));
	public static final DeferredBlock<Block> MARKED_CHAOTIC_INJECTOR = BLOCKS.register("marked_chaotic_injector", ()->new MarkedInjectorBlock(3));
}
