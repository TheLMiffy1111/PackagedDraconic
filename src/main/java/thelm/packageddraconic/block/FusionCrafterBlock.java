package thelm.packageddraconic.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import thelm.packagedauto.block.BaseBlock;
import thelm.packageddraconic.PackagedDraconic;
import thelm.packageddraconic.tile.FusionCrafterTile;

public class FusionCrafterBlock extends BaseBlock {

	public static final FusionCrafterBlock INSTANCE = new FusionCrafterBlock();
	public static final Item ITEM_INSTANCE = new BlockItem(INSTANCE, new Item.Properties().tab(PackagedDraconic.ITEM_GROUP)).setRegistryName("packageddraconic:fusion_crafter");
	public static final VoxelShape SHAPE = box(1, 1, 1, 15, 15, 15);

	public FusionCrafterBlock() {
		super(AbstractBlock.Properties.of(Material.METAL).strength(15F, 25F).noOcclusion().sound(SoundType.METAL));
		setRegistryName("packageddraconic:fusion_crafter");
	}

	@Override
	public FusionCrafterTile createTileEntity(BlockState state, IBlockReader worldIn) {
		return FusionCrafterTile.TYPE_INSTANCE.create();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
}
