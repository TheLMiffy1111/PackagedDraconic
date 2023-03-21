package thelm.packageddraconic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thelm.packagedauto.block.BaseBlock;
import thelm.packagedauto.block.entity.BaseBlockEntity;
import thelm.packageddraconic.PackagedDraconic;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;

public class FusionCrafterBlock extends BaseBlock {

	public static final FusionCrafterBlock INSTANCE = new FusionCrafterBlock();
	public static final Item ITEM_INSTANCE = new BlockItem(INSTANCE, new Item.Properties().tab(PackagedDraconic.ITEM_GROUP)).setRegistryName("packageddraconic:fusion_crafter");
	public static final VoxelShape SHAPE = box(1, 1, 1, 15, 15, 15);

	public FusionCrafterBlock() {
		super(BlockBehaviour.Properties.of(Material.METAL).strength(15F, 25F).noOcclusion().sound(SoundType.METAL));
		setRegistryName("packageddraconic:fusion_crafter");
	}

	@Override
	public FusionCrafterBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return FusionCrafterBlockEntity.TYPE_INSTANCE.create(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return BaseBlockEntity::tick;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if(state.getBlock() == newState.getBlock()) {
			return;
		}
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof FusionCrafterBlockEntity crafter && crafter.isWorking) {
			crafter.cancelCraft();
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
}
