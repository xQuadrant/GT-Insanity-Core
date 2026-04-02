package GTInsanityCore.common.blocks;

import GTInsanityCore.common.tileentities.TileEntityLaserPort;
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

public class BlockLaserPort extends Block {

    public BlockLaserPort() {
        super(Material.IRON);
        setHardness(4.0f);
        setResistance(15.0f);
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
        return new TileEntityLaserPort();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof TileEntityLaserPort)) {
            return false;
        }

        TileEntityLaserPort port = (TileEntityLaserPort) tileEntity;
        if (ToolHelper.isTool(player.getHeldItem(hand), ToolClasses.SOFT_MALLET)) {
            port.clearStoredLaserUnits();
            player.sendStatusMessage(new TextComponentString("Laser Port cleared."), false);
            return true;
        }

        if (ToolHelper.isTool(player.getHeldItem(hand), ToolClasses.SCREWDRIVER)
                || ToolHelper.isTool(player.getHeldItem(hand), ToolClasses.WRENCH)) {
            player.sendStatusMessage(new TextComponentString(
                    "Laser Port status: " + port.getStoredLaserUnits() + " / " + port.getLaserUnitCapacity() + " LU"),
                    false);
            return true;
        }

        if (player.isSneaking()) {
            long inserted = port.receiveLaserUnits(port.getLaserUnitCapacity(), false);
            player.sendStatusMessage(new TextComponentString(
                    "Laser Port charged with " + inserted + " LU. Stored: " + port.getStoredLaserUnits() +
                            " / " + port.getLaserUnitCapacity()), false);
        } else {
            player.sendStatusMessage(new TextComponentString(
                    "Laser Port status: " + port.getStoredLaserUnits() + " / " + port.getLaserUnitCapacity() + " LU"),
                    false);
        }
        return true;
    }
}
