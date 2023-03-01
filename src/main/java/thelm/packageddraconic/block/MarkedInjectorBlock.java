package thelm.packageddraconic.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thelm.packagedauto.block.BaseBlock;
import thelm.packageddraconic.PackagedDraconic;
import thelm.packageddraconic.tile.MarkedInjectorTile;

public class MarkedInjectorBlock extends BaseBlock {

	public static final MarkedInjectorBlock INSTANCE = new MarkedInjectorBlock();
	public static final Item ITEM_INSTANCE = new BlockItem(INSTANCE, new Item.Properties().tab(PackagedDraconic.ITEM_GROUP)).setRegistryName("packageddraconic:marked_injector");
	public static final VoxelShape SHAPE_DOWN = box(1, 6, 1, 15, 16, 15);
	public static final VoxelShape SHAPE_UP = box(1, 0, 1, 15, 10, 15);
	public static final VoxelShape SHAPE_NORTH = box(1, 1, 6, 15, 15, 16);
	public static final VoxelShape SHAPE_SOUTH = box(1, 1, 0, 15, 15, 10);
	public static final VoxelShape SHAPE_WEST = box(6, 1, 1, 16, 15, 15);
	public static final VoxelShape SHAPE_EAST = box(0, 1, 1, 10, 15, 15);

	public MarkedInjectorBlock() {
		super(AbstractBlock.Properties.of(Material.METAL).strength(15F, 25F).noOcclusion().sound(SoundType.METAL));
		registerDefaultState(stateDefinition.any().setValue(DirectionalBlock.FACING, Direction.UP));
		setRegistryName("packageddraconic:marked_injector");
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(DirectionalBlock.FACING);
	}

	@Override
	public MarkedInjectorTile createTileEntity(BlockState state, IBlockReader worldIn) {
		return MarkedInjectorTile.TYPE_INSTANCE.create();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return defaultBlockState().setValue(DirectionalBlock.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Direction facing = state.getValue(DirectionalBlock.FACING);
		switch(facing) {
		case DOWN: return SHAPE_DOWN;
		case UP: return SHAPE_UP;
		case NORTH: return SHAPE_NORTH;
		case SOUTH: return SHAPE_SOUTH;
		case WEST: return SHAPE_WEST;
		case EAST: return SHAPE_EAST;
		}
		return super.getShape(state, worldIn, pos, context);
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult rayTraceResult) {
		return ActionResultType.PASS;
	}
}
