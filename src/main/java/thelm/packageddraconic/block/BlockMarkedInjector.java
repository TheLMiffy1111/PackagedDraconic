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

	public static final BlockMarkedInjector BASIC = new BlockMarkedInjector(0, "packageddraconic:marked_basic_injector");
	public static final BlockMarkedInjector WYVERN = new BlockMarkedInjector(1, "packageddraconic:marked_wyvern_injector");
	public static final BlockMarkedInjector DRACONIC = new BlockMarkedInjector(2, "packageddraconic:marked_draconic_injector");
	public static final BlockMarkedInjector CHAOTIC = new BlockMarkedInjector(3, "packageddraconic:marked_chaotic_injector");
	public static final Item BASIC_ITEM = new ItemBlock(BASIC).setRegistryName("packageddraconic:marked_basic_injector");
	public static final Item WYVERN_ITEM = new ItemBlock(WYVERN).setRegistryName("packageddraconic:marked_wyvern_injector");
	public static final Item DRACONIC_ITEM = new ItemBlock(DRACONIC).setRegistryName("packageddraconic:marked_draconic_injector");
	public static final Item CHAOTIC_ITEM = new ItemBlock(CHAOTIC).setRegistryName("packageddraconic:marked_chaotic_injector");
	public static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.0625, 0.375, 0.0625, 0.9375, 1, 0.9375);
	public static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 0.625, 0.9375);
	public static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0625, 0.0625, 0.375, 0.9375, 0.9375, 1);
	public static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.0625, 0.0625, 0, 0.9375, 0.9375, 0.625);
	public static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.375, 0.0625, 0.0625, 1, 0.9375, 0.9375);
	public static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0, 0.0625, 0.0625, 0.625, 0.9375, 0.9375);

	public final int tier;

	public BlockMarkedInjector(int tier, String name) {
		super(Material.IRON);
		setDefaultState(blockState.getBaseState().withProperty(BlockDirectional.FACING, EnumFacing.UP));
		setHardness(15F);
		setResistance(25F);
		setSoundType(SoundType.METAL);
		setTranslationKey(name.replace(':', '.'));
		setRegistryName(name);
		setCreativeTab(PackagedDraconic.CREATIVE_TAB);
		this.tier = tier;
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
		case DOWN: return AABB_DOWN;
		case UP: return AABB_UP;
		case NORTH: return AABB_NORTH;
		case SOUTH: return AABB_SOUTH;
		case WEST: return AABB_WEST;
		case EAST: return AABB_EAST;
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
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "facing=up"));
	}
}
