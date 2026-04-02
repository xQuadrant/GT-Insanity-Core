package GTInsanityCore.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public abstract class AbstractPatternedMultiblockTileEntity extends TileEntity implements ITickable {

    private static final String NBT_FORMED = "Formed";
    private static final String NBT_LAST_STRUCTURE_CHECK = "LastStructureCheck";

    protected boolean formed;
    protected long lastStructureCheck = -1L;

    @Override
    public final void update() {
        if (world == null || world.isRemote) {
            return;
        }

        if (shouldRefreshStructure()) {
            refreshStructure();
        }

        if (formed) {
            updateFormed();
        } else {
            updateUnformed();
        }
    }

    protected boolean shouldRefreshStructure() {
        return world.getTotalWorldTime() % getStructureCheckInterval() == 0L;
    }

    protected int getStructureCheckInterval() {
        return 20;
    }

    public void refreshStructure() {
        boolean newFormedState = checkStructurePattern();
        boolean changed = formed != newFormedState;
        formed = newFormedState;
        lastStructureCheck = world == null ? -1L : world.getTotalWorldTime();
        if (changed) {
            onStructureStateChanged(newFormedState);
        }
        markDirty();
    }

    protected void updateFormed() {
    }

    protected void updateUnformed() {
    }

    protected void onStructureStateChanged(boolean newFormedState) {
    }

    protected abstract boolean checkStructurePattern();

    protected String formatStructureState(String machineName) {
        return machineName + " [" + (formed ? "Formed" : "Incomplete") + "]";
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean(NBT_FORMED, formed);
        compound.setLong(NBT_LAST_STRUCTURE_CHECK, lastStructureCheck);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        formed = compound.getBoolean(NBT_FORMED);
        lastStructureCheck = compound.getLong(NBT_LAST_STRUCTURE_CHECK);
    }
}
