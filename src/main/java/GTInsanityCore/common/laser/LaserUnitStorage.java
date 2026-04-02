package GTInsanityCore.common.laser;

import net.minecraft.nbt.NBTTagCompound;

public class LaserUnitStorage {

    private final long capacity;
    private final long maxReceive;
    private final long maxExtract;
    private long stored;

    public LaserUnitStorage(long capacity, long maxReceive, long maxExtract) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public long getStored() {
        return stored;
    }

    public long getCapacity() {
        return capacity;
    }

    public long receive(long amount, boolean simulate) {
        if (amount <= 0L) {
            return 0L;
        }
        long accepted = Math.min(capacity - stored, Math.min(maxReceive, amount));
        if (!simulate && accepted > 0L) {
            stored += accepted;
        }
        return accepted;
    }

    public long extract(long amount, boolean simulate) {
        if (amount <= 0L) {
            return 0L;
        }
        long extracted = Math.min(stored, Math.min(maxExtract, amount));
        if (!simulate && extracted > 0L) {
            stored -= extracted;
        }
        return extracted;
    }

    public void setStored(long stored) {
        this.stored = Math.max(0L, Math.min(capacity, stored));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag, String key) {
        tag.setLong(key, stored);
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag, String key) {
        if (tag.hasKey(key)) {
            setStored(tag.getLong(key));
        }
    }
}
