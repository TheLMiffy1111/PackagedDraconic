package thelm.packageddraconic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thelm.packagedauto.block.BaseBlock;
import thelm.packagedauto.block.entity.BaseBlockEntity;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;
import thelm.packageddraconic.block.entity.PackagedDraconicBlockEntities;

public class FusionCrafterBlock extends BaseBlock {

	public static final VoxelShape SHAPE = box(1, 1, 1, 15, 15, 15);

	public FusionCrafterBlock() {
		super(BlockBehaviour.Properties.of().strength(10F, 15F).noOcclusion().mapColor(MapColor.METAL).sound(SoundType.METAL));
	}

	@Override
	public FusionCrafterBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return PackagedDraconicBlockEntities.FUSION_CRAFTER.get().create(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return BaseBlockEntity::tick;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if(player.isShiftKeyDown()) {
			if(level.getBlockEntity(pos) instanceof FusionCrafterBlockEntity crafter && !crafter.isWorking) {
				if(!level.isClientSide) {
					Component message = crafter.getMessage();
					if(message != null) {
						player.sendSystemMessage(message);
					}
				}
				return InteractionResult.SUCCESS;
			}
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
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
