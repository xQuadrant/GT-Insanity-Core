package GTInsanityCore.common.tileentities;

import GTInsanityCore.common.blocks.BlockLaserCasing;
import GTInsanityCore.common.blocks.BlockLaserPort;
import GTInsanityCore.common.laser.ILaserUnitHolder;
import GTInsanityCore.common.laser.LaserUnitStorage;
import GTInsanityCore.common.laser.LaserUnitTiers;
import gregtech.api.GTValues;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntitySimpleLaserController extends AbstractPatternedMultiblockTileEntity implements ILaserUnitHolder {

    private static final String NBT_LASER_UNITS = "LaserUnits";
    private static final String NBT_PORTS = "ConnectedPorts";
    private static final String NBT_PROGRESS = "Progress";
    private static final String NBT_CYCLES = "CompletedCycles";

    private static final long LU_PER_OPERATION = 64L;
    private static final long PORT_PULL_PER_TICK = LaserUnitTiers.transferRate(GTValues.HV);
    private static final int MAX_PROGRESS = 100;

    private final LaserUnitStorage storage = new LaserUnitStorage(
            LaserUnitTiers.bufferForTier(GTValues.EV),
            LaserUnitTiers.transferRate(GTValues.EV),
            LaserUnitTiers.transferRate(GTValues.EV));

    private int connectedPorts = 0;
    private int progress = 0;
    private int completedCycles = 0;

    @Override
    protected boolean checkStructurePattern() {
        if (world == null) {
            return false;
        }

        int portsFound = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                BlockPos targetPos = pos.add(dx, 0, dz);
                Block block = world.getBlockState(targetPos).getBlock();
                if (block instanceof BlockLaserPort) {
                    portsFound++;
                } else if (!(block instanceof BlockLaserCasing)) {
                    connectedPorts = 0;
                    return false;
                }
            }
        }

        connectedPorts = portsFound;
        return portsFound > 0;
    }

    @Override
    protected void updateFormed() {
        pullLaserUnitsFromPorts(PORT_PULL_PER_TICK);
        if (storage.getStored() < LU_PER_OPERATION) {
            if (progress > 0) {
                progress = 0;
                markDirty();
            }
            return;
        }

        storage.extract(LU_PER_OPERATION, false);
        progress++;
        if (progress >= MAX_PROGRESS) {
            progress = 0;
            completedCycles++;
        }
        markDirty();
    }

    @Override
    protected void updateUnformed() {
        connectedPorts = 0;
        if (progress > 0) {
            progress = 0;
            markDirty();
        }
    }

    private void pullLaserUnitsFromPorts(long maxAmount) {
        long remaining = maxAmount;
        for (int dx = -1; dx <= 1 && remaining > 0; dx++) {
            for (int dz = -1; dz <= 1 && remaining > 0; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                TileEntity tileEntity = world.getTileEntity(pos.add(dx, 0, dz));
                if (!(tileEntity instanceof TileEntityLaserPort)) {
                    continue;
                }

                TileEntityLaserPort port = (TileEntityLaserPort) tileEntity;
                long extracted = port.extractLaserUnits(remaining, false);
                if (extracted > 0) {
                    receiveLaserUnits(extracted, false);
                    remaining -= extracted;
                }
            }
        }
    }

    public String getDebugStatus() {
        return formatStructureState("Simple Laser Array") + " "
                + "Ports=" + connectedPorts
                + ", Stored=" + getStoredLaserUnits() + "/" + getLaserUnitCapacity() + " LU"
                + ", Transfer=" + PORT_PULL_PER_TICK + " LU/t"
                + ", Progress=" + progress + "/" + MAX_PROGRESS
                + ", Cycles=" + completedCycles
                + ", LastCheck=" + lastStructureCheck;
    }

    @Override
    public long getStoredLaserUnits() {
        return storage.getStored();
    }

    @Override
    public long getLaserUnitCapacity() {
        return storage.getCapacity();
    }

    @Override
    public long receiveLaserUnits(long amount, boolean simulate) {
        long received = storage.receive(amount, simulate);
        if (!simulate && received > 0) {
            markDirty();
        }
        return received;
    }

    @Override
    public long extractLaserUnits(long amount, boolean simulate) {
        long extracted = storage.extract(amount, simulate);
        if (!simulate && extracted > 0) {
            markDirty();
        }
        return extracted;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        storage.writeToNBT(compound, NBT_LASER_UNITS);
        compound.setInteger(NBT_PORTS, connectedPorts);
        compound.setInteger(NBT_PROGRESS, progress);
        compound.setInteger(NBT_CYCLES, completedCycles);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        storage.readFromNBT(compound, NBT_LASER_UNITS);
        connectedPorts = compound.getInteger(NBT_PORTS);
        progress = compound.getInteger(NBT_PROGRESS);
        completedCycles = compound.getInteger(NBT_CYCLES);
    }
}
