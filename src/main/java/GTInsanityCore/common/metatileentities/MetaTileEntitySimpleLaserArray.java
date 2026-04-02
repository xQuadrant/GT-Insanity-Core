package GTInsanityCore.common.metatileentities;

import GTInsanityCore.common.blocks.GTIBlocks;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.ILaserContainer;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;
import java.util.List;

public class MetaTileEntitySimpleLaserArray extends MultiblockWithDisplayBase implements IControllable {

    private static final String NBT_WORKING_ENABLED = "WorkingEnabled";
    private static final String NBT_ACTIVE = "Active";
    private static final String NBT_PROGRESS = "Progress";
    private static final String NBT_COMPLETED_CYCLES = "CompletedCycles";
    private static final long LU_PER_TICK = 64L;
    private static final int MAX_PROGRESS = 100;

    private boolean workingEnabled = true;
    private boolean active = false;
    private int progress = 0;
    private int completedCycles = 0;

    public MetaTileEntitySimpleLaserArray(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntitySimpleLaserArray(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXX", "XSX", "XXX")
                .where('S', selfPredicate())
                .where('X', states(getLaserCasingState())
                        .or(abilities(MultiblockAbility.INPUT_LASER)
                                .setMinGlobalLimited(1)
                                .setMaxGlobalLimited(8)
                                .setPreviewCount(1)))
                .build();
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        List<MultiblockShapeInfo> shapes = new ArrayList<>();
        for (MetaTileEntityGTILaserHatch hatch : GTIMetaTileEntities.LASER_INPUT_HATCHES) {
            if (hatch == null) {
                continue;
            }
            shapes.add(MultiblockShapeInfo.builder()
                    .aisle("XXX", "XSX", "XLX")
                    .where('S', this, EnumFacing.NORTH)
                    .where('X', getLaserCasingState())
                    .where('L', hatch, EnumFacing.SOUTH)
                    .build());
        }
        return shapes;
    }

    @Override
    protected void updateFormedValid() {
        if (!workingEnabled) {
            active = false;
            resetProgress();
            return;
        }

        long stored = getStoredLaserUnits();
        if (stored < LU_PER_TICK) {
            active = false;
            resetProgress();
            return;
        }

        drainLaserUnits(LU_PER_TICK);
        active = true;
        progress++;
        if (progress >= MAX_PROGRESS) {
            progress = 0;
            completedCycles++;
        }
        markDirty();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        active = false;
        resetProgress();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(active, workingEnabled)
                .addCustom(lines -> {
                    lines.add(new TextComponentString("Laser Hatches: " + getAbilities(MultiblockAbility.INPUT_LASER).size()));
                    lines.add(new TextComponentString("Stored LU: " + getStoredLaserUnits() + " / " + getLaserCapacity()));
                    lines.add(new TextComponentString("Drain: " + LU_PER_TICK + " LU/t"));
                    lines.add(new TextComponentString("Progress: " + progress + "/" + MAX_PROGRESS));
                    lines.add(new TextComponentString("Completed Cycles: " + completedCycles));
                });
        super.addDisplayText(textList);
    }

    @Override
    public ICubeRenderer getBaseTexture(gregtech.api.metatileentity.multiblock.IMultiblockPart sourcePart) {
        return Textures.HIGH_POWER_CASING;
    }

    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.DATA_BANK_OVERLAY;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        this.workingEnabled = workingEnabled;
        markDirty();
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE) {
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean(NBT_WORKING_ENABLED, workingEnabled);
        data.setBoolean(NBT_ACTIVE, active);
        data.setInteger(NBT_PROGRESS, progress);
        data.setInteger(NBT_COMPLETED_CYCLES, completedCycles);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        workingEnabled = data.getBoolean(NBT_WORKING_ENABLED);
        active = data.getBoolean(NBT_ACTIVE);
        progress = data.getInteger(NBT_PROGRESS);
        completedCycles = data.getInteger(NBT_COMPLETED_CYCLES);
    }

    @Override
    public String[] getDescription() {
        return new String[] {"gtinsanitycore.machine.simple_laser_array.tooltip"};
    }

    private long getStoredLaserUnits() {
        long total = 0L;
        for (ILaserContainer container : getAbilities(MultiblockAbility.INPUT_LASER)) {
            total += container.getEnergyStored();
        }
        return total;
    }

    private long getLaserCapacity() {
        long total = 0L;
        for (ILaserContainer container : getAbilities(MultiblockAbility.INPUT_LASER)) {
            total += container.getEnergyCapacity();
        }
        return total;
    }

    private void drainLaserUnits(long amount) {
        long remaining = amount;
        for (ILaserContainer container : getAbilities(MultiblockAbility.INPUT_LASER)) {
            if (remaining <= 0L) {
                return;
            }
            long drained = container.removeEnergy(remaining);
            remaining -= drained;
        }
    }

    private void resetProgress() {
        if (progress != 0) {
            progress = 0;
            markDirty();
        }
    }

    private static IBlockState getLaserCasingState() {
        return GTIBlocks.LASER_CASING.getDefaultState();
    }
}
