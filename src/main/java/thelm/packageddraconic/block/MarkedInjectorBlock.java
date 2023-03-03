package thelm.packageddraconic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thelm.packagedauto.block.BaseBlock;
import thelm.packageddraconic.PackagedDraconic;
import thelm.packageddraconic.block.entity.MarkedInjectorBlockEntity;

public class MarkedInjectorBlock extends BaseBlock {

	public static final MarkedInjectorBlock BASIC = new MarkedInjectorBlock(0, "packageddraconic:marked_draconium_injector");
	public static final MarkedInjectorBlock WYVERN = new MarkedInjectorBlock(1, "packageddraconic:marked_wyvern_injector");
	public static final MarkedInjectorBlock DRACONIC = new MarkedInjectorBlock(2, "packageddraconic:marked_draconic_injector");
	public static final MarkedInjectorBlock CHAOTIC = new MarkedInjectorBlock(3, "packageddraconic:marked_chaotic_injector");
	public static final Item BASIC_ITEM = new BlockItem(BASIC, new Item.Properties().tab(PackagedDraconic.ITEM_GROUP)).setRegistryName("packageddraconic:marked_draconium_injector");
	public static final Item WYVERN_ITEM = new BlockItem(WYVERN, new Item.Properties().tab(PackagedDraconic.ITEM_GROUP)).setRegistryName("packageddraconic:marked_wyvern_injector");
	public static final Item DRACONIC_ITEM = new BlockItem(DRACONIC, new Item.Properties().tab(PackagedDraconic.ITEM_GROUP)).setRegistryName("packageddraconic:marked_draconic_injector");
	public static final Item CHAOTIC_ITEM = new BlockItem(CHAOTIC, new Item.Properties().tab(PackagedDraconic.ITEM_GROUP)).setRegistryName("packageddraconic:marked_chaotic_injector");
	public static final VoxelShape SHAPE_DOWN = box(1, 6, 1, 15, 16, 15);
	public static final VoxelShape SHAPE_UP = box(1, 0, 1, 15, 10, 15);
	public static final VoxelShape SHAPE_NORTH = box(1, 1, 6, 15, 15, 16);
	public static final VoxelShape SHAPE_SOUTH = box(1, 1, 0, 15, 15, 10);
	public static final VoxelShape SHAPE_WEST = box(6, 1, 1, 16, 15, 15);
	public static final VoxelShape SHAPE_EAST = box(0, 1, 1, 10, 15, 15);

	public final int tier;

	public MarkedInjectorBlock(int tier, String name) {
		super(BlockBehaviour.Properties.of(Material.METAL).strength(15F, 25F).noOcclusion().sound(SoundType.METAL));
		registerDefaultState(stateDefinition.any().setValue(DirectionalBlock.FACING, Direction.UP));
		setRegistryName(name);
		this.tier = tier;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DirectionalBlock.FACING);
	}

	@Override
	public MarkedInjectorBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return MarkedInjectorBlockEntity.TYPE_INSTANCE.create(pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(DirectionalBlock.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(DirectionalBlock.FACING);
		return switch(facing) {
		case DOWN -> SHAPE_DOWN;
		case UP -> SHAPE_UP;
		case NORTH -> SHAPE_NORTH;
		case SOUTH -> SHAPE_SOUTH;
		case WEST -> SHAPE_WEST;
		case EAST -> SHAPE_EAST;
		default -> super.getShape(state, level, pos, context);
		};
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		return InteractionResult.PASS;
	}
}
