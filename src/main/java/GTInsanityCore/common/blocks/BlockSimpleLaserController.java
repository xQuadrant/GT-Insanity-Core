package GTInsanityCore.common.blocks;

import GTInsanityCore.common.tileentities.TileEntitySimpleLaserController;
import gregtech.api.items.toolitem.ToolClasses;
import gregtech.api.items.toolitem.ToolHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockSimpleLaserController extends Block {

    public BlockSimpleLaserController() {
        super(Material.IRON);
        setHardness(5.0f);
        setResistance(20.0f);
        setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.METAL);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntitySimpleLaserController();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof TileEntitySimpleLaserController)) {
            return false;
        }

        TileEntitySimpleLaserController controller = (TileEntitySimpleLaserController) tileEntity;
        if (ToolHelper.isTool(player.getHeldItem(hand), ToolClasses.WRENCH) || player.isSneaking()) {
            controller.refreshStructure();
            player.sendStatusMessage(new TextComponentString("Structure check complete."), false);
            return true;
        }

        if (ToolHelper.isTool(player.getHeldItem(hand), ToolClasses.SCREWDRIVER)
                || ToolHelper.isTool(player.getHeldItem(hand), ToolClasses.SOFT_MALLET)) {
            player.sendStatusMessage(new TextComponentString(controller.getDebugStatus()), false);
            return true;
        }

        player.sendStatusMessage(new TextComponentString(controller.getDebugStatus()), false);
        return true;
    }
}
