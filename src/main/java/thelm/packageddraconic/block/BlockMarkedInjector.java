package thelm.packageddraconic.block;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packagedauto.block.BlockBase;
import thelm.packagedauto.tile.TileBase;
import thelm.packageddraconic.PackagedDraconic;
import thelm.packageddraconic.tile.TileMarkedInjector;

public class BlockMarkedInjector extends BlockBase {

	public static final BlockMarkedInjector INSTANCE = new BlockMarkedInjector();
	public static final Item ITEM_INSTANCE = new ItemBlock(INSTANCE).setRegistryName("packageddraconic:marked_injector");
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("packageddraconic:marked_injector#facing=up");

	public BlockMarkedInjector() {
		super(Material.IRON);
		setDefaultState(blockState.getBaseState().withProperty(BlockDirectional.FACING, EnumFacing.UP));
		setHardness(15F);
		setResistance(25F);
		setSoundType(SoundType.METAL);
		setTranslationKey("packageddraconic.marked_injector");
		setRegistryName("marked_injector");
		setCreativeTab(PackagedDraconic.CREATIVE_TAB);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockDirectional.FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.byIndex(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BlockDirectional.FACING).getIndex();
	}

	@Override
	public TileBase createNewTileEntity(World worldIn, int meta) {
		return new TileMarkedInjector();
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing facing = state.getValue(BlockDirectional.FACING);
		switch(facing) {
		case DOWN: return new AxisAlignedBB(0.0625, 0.375, 0.0625, 0.9375, 1, 0.9375);
		case UP: return new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 0.625, 0.9375);
		case NORTH: return new AxisAlignedBB(0.0625, 0.0625, 0.375, 0.9375, 0.9375, 1);
		case SOUTH: return new AxisAlignedBB(0.0625, 0.0625, 0, 0.9375, 0.9375, 0.625);
		case WEST: return new AxisAlignedBB(0.375, 0.0625, 0.0625, 1, 0.9375, 0.9375);
		case EAST: return new AxisAlignedBB(0, 0.0625, 0.0625, 0.625, 0.9375, 0.9375);
		}
		return super.getBoundingBox(state, source, pos);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(ITEM_INSTANCE, 0, MODEL_LOCATION);
	}
}
