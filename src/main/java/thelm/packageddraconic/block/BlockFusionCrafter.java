package thelm.packageddraconic.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packagedauto.block.BlockBase;
import thelm.packagedauto.tile.TileBase;
import thelm.packageddraconic.PackagedDraconic;
import thelm.packageddraconic.tile.TileFusionCrafter;

public class BlockFusionCrafter extends BlockBase {

	public static final BlockFusionCrafter INSTANCE = new BlockFusionCrafter();
	public static final Item ITEM_INSTANCE = new ItemBlock(INSTANCE).setRegistryName("packageddraconic:fusion_crafter");
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("packageddraconic:fusion_crafter#normal");
	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375);

	public BlockFusionCrafter() {
		super(Material.IRON);
		setHardness(15F);
		setResistance(25F);
		setSoundType(SoundType.METAL);
		setTranslationKey("packageddraconic.fusion_crafter");
		setRegistryName("packageddraconic:fusion_crafter");
		setCreativeTab(PackagedDraconic.CREATIVE_TAB);
	}

	@Override
	public TileBase createNewTileEntity(World worldIn, int meta) {
		return new TileFusionCrafter();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.isSneaking()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if(tileentity instanceof TileFusionCrafter) {
				TileFusionCrafter crafter = (TileFusionCrafter)tileentity;
				if(!crafter.isWorking) {
					if(!worldIn.isRemote) {
						ITextComponent message = crafter.getMessage();
						if(message != null) {
							playerIn.sendMessage(message);
						}
					}
					return true;
				}
			}
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if(tileentity instanceof TileFusionCrafter) {
			TileFusionCrafter crafter = (TileFusionCrafter)tileentity;
			if(crafter.isWorking) {
				crafter.endProcess();
			}
		}
		super.breakBlock(worldIn, pos, state);
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(ITEM_INSTANCE, 0, MODEL_LOCATION);
	}
}
