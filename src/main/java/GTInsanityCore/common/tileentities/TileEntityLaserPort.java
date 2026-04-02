package GTInsanityCore.common.tileentities;

import GTInsanityCore.common.laser.ILaserUnitHolder;
import GTInsanityCore.common.laser.LaserUnitStorage;
import GTInsanityCore.common.laser.LaserUnitTiers;
import gregtech.api.GTValues;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityLaserPort extends TileEntity implements ILaserUnitHolder {

    private static final String NBT_LASER_UNITS = "LaserUnits";

    private final LaserUnitStorage storage = new LaserUnitStorage(
            LaserUnitTiers.bufferForTier(GTValues.HV),
            LaserUnitTiers.transferRate(GTValues.HV),
            LaserUnitTiers.transferRate(GTValues.HV));

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

    public void clearStoredLaserUnits() {
        storage.setStored(0L);
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        storage.writeToNBT(compound, NBT_LASER_UNITS);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        storage.readFromNBT(compound, NBT_LASER_UNITS);
    }
}
