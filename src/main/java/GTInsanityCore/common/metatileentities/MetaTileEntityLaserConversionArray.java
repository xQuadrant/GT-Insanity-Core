package GTInsanityCore.common.metatileentities;

import GTInsanityCore.API.capability.ILaserUnitContainer;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;
import java.util.List;

public class MetaTileEntityLaserConversionArray extends MultiblockWithDisplayBase implements IControllable {

    private static final String NBT_WORKING_ENABLED = "WorkingEnabled";
    private static final String NBT_ACTIVE = "Active";
    private static final String NBT_PROGRESS = "Progress";
    private static final String NBT_COMPLETED_CYCLES = "CompletedCycles";
    private static final String NBT_LAST_EU_CONSUMED = "LastEUConsumed";
    private static final String NBT_LAST_LU_PRODUCED = "LastLUProduced";
    private static final int MAX_PROGRESS = 100;

    private boolean workingEnabled = true;
    private boolean active = false;
    private int progress = 0;
    private int completedCycles = 0;
    private long lastEUConsumed = 0L;
    private long lastLUProduced = 0L;

    public MetaTileEntityLaserConversionArray(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityLaserConversionArray(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("HHH", "HOH", "HHH")
                .aisle("HHH", "H#L", "HHH")
                .aisle("HHH", "HSE", "HHH")
                .where('S', selfPredicate())
                .where('H', states(getHighEnergyCasingState()).setMinGlobalLimited(23))
                .where('E', abilities(gregtech.api.metatileentity.multiblock.MultiblockAbility.INPUT_ENERGY)
                        .setMinGlobalLimited(1).setPreviewCount(1))
                .where('L', abilities(GTIMultiblockAbilities.LASER_OUTPUT_LU)
                        .setMinGlobalLimited(1).setPreviewCount(1))
                .where('O', abilities(GTIMultiblockAbilities.LASER_OUTPUT_LU)
                        .setMinGlobalLimited(1).setPreviewCount(1))
                .where('#', air())
                .build();
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        List<MultiblockShapeInfo> shapes = new ArrayList<>();
        for (int tier = GTValues.LV; tier <= GTValues.OpV; tier++) {
            MetaTileEntityGTILaserOutputHatch hatch = GTIMetaTileEntities.LASER_OUTPUT_HATCHES[tier];
            if (hatch == null || MetaTileEntities.ENERGY_INPUT_HATCH[tier] == null) {
                continue;
            }
            shapes.add(MultiblockShapeInfo.builder()
                    .aisle("HHH", "HOH", "HHH")
                    .aisle("HHH", "H#L", "HHH")
                    .aisle("HHH", "HSE", "HHH")
                    .where('S', this, EnumFacing.NORTH)
                    .where('H', getHighEnergyCasingState())
                    .where('E', MetaTileEntities.ENERGY_INPUT_HATCH[tier], EnumFacing.EAST)
                    .where('L', hatch, EnumFacing.EAST)
                    .where('O', hatch, EnumFacing.NORTH)
                    .where('#', net.minecraft.init.Blocks.AIR.getDefaultState())
                    .build());
        }
        return shapes;
    }

    @Override
    protected void updateFormedValid() {
        if (!workingEnabled) {
            active = false;
            resetProgress();
            clearLastTransfer();
            return;
        }

        ConversionState state = performConversion(false);
        if (state.producedLU <= 0L) {
            active = false;
            resetProgress();
            clearLastTransfer();
            return;
        }

        active = true;
        lastEUConsumed = state.consumedEU;
        lastLUProduced = state.producedLU;
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
        clearLastTransfer();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        int operatingTier = getOperatingTier();
        int efficiency = getEfficiencyForTier(operatingTier);
        MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(active, workingEnabled)
                .addCustom(lines -> {
                    if (!isStructureFormed()) {
                        return;
                    }
                    lines.add(new TextComponentString("Operating Tier: " + GTValues.VNF[operatingTier]));
                    lines.add(new TextComponentString("Efficiency: " + efficiency + "%"));
                    lines.add(new TextComponentString("EU -> LU: " + lastEUConsumed + " EU/t -> " + lastLUProduced + " LU/t"));
                    lines.add(new TextComponentString("Stored LU: " + getStoredLaserUnits() + " / " + getLaserCapacity()));
                    lines.add(new TextComponentString("Progress: " + progress + "/" + MAX_PROGRESS));
                    lines.add(new TextComponentString("Completed Cycles: " + completedCycles));
                });
        super.addDisplayText(textList);
    }

    @Override
    public ICubeRenderer getBaseTexture(gregtech.api.metatileentity.multiblock.IMultiblockPart sourcePart) {
        return Textures.ADVANCED_COMPUTER_CASING;
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
        data.setLong(NBT_LAST_EU_CONSUMED, lastEUConsumed);
        data.setLong(NBT_LAST_LU_PRODUCED, lastLUProduced);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        workingEnabled = data.getBoolean(NBT_WORKING_ENABLED);
        active = data.getBoolean(NBT_ACTIVE);
        progress = data.getInteger(NBT_PROGRESS);
        completedCycles = data.getInteger(NBT_COMPLETED_CYCLES);
        lastEUConsumed = data.getLong(NBT_LAST_EU_CONSUMED);
        lastLUProduced = data.getLong(NBT_LAST_LU_PRODUCED);
    }

    @Override
    public String[] getDescription() {
        return new String[] {"gtinsanitycore.machine.laser_conversion_array.tooltip"};
    }

    private long getStoredLaserUnits() {
        long total = 0L;
        for (ILaserUnitContainer container : getAbilities(GTIMultiblockAbilities.LASER_OUTPUT_LU)) {
            total += container.getStoredLaserUnits();
        }
        return total;
    }

    private long getLaserCapacity() {
        long total = 0L;
        for (ILaserUnitContainer container : getAbilities(GTIMultiblockAbilities.LASER_OUTPUT_LU)) {
            total += container.getLaserUnitCapacity();
        }
        return total;
    }

    private int getOperatingTier() {
        int tier = GTValues.LV;
        boolean foundAny = false;
        for (IEnergyContainer container : getAbilities(MultiblockAbility.INPUT_ENERGY)) {
            tier = foundAny ? Math.min(tier, GTUtility.getTierByVoltage(container.getInputVoltage()))
                    : GTUtility.getTierByVoltage(container.getInputVoltage());
            foundAny = true;
        }
        for (ILaserUnitContainer container : getAbilities(GTIMultiblockAbilities.LASER_OUTPUT_LU)) {
            tier = foundAny ? Math.min(tier, container.getLaserTier()) : container.getLaserTier();
            foundAny = true;
        }
        return foundAny ? tier : GTValues.LV;
    }

    private int getEfficiencyForTier(int tier) {
        return Math.max(1, 100 - ((tier - GTValues.LV) * 2));
    }

    private ConversionState performConversion(boolean simulate) {
        List<IEnergyContainer> energyInputs = getAbilities(MultiblockAbility.INPUT_ENERGY);
        List<ILaserUnitContainer> laserOutputs = getAbilities(GTIMultiblockAbilities.LASER_OUTPUT_LU);
        if (energyInputs.isEmpty() || laserOutputs.isEmpty()) {
            return ConversionState.EMPTY;
        }

        long totalStoredEU = 0L;
        long totalEUPerTick = 0L;
        for (IEnergyContainer container : energyInputs) {
            totalStoredEU += Math.max(0L, container.getEnergyStored());
            totalEUPerTick += Math.max(0L, container.getInputVoltage() * Math.max(1L, container.getInputAmperage()));
        }

        long totalLaserSpace = 0L;
        for (ILaserUnitContainer container : laserOutputs) {
            totalLaserSpace += Math.max(0L, container.getLaserUnitCapacity() - container.getStoredLaserUnits());
        }

        int operatingTier = getOperatingTier();
        int efficiency = getEfficiencyForTier(operatingTier);
        long euToConsume = Math.min(totalStoredEU, totalEUPerTick);
        euToConsume = Math.min(euToConsume, (totalLaserSpace * 100L) / efficiency);
        long luToProduce = (euToConsume * efficiency) / 100L;

        if (euToConsume <= 0L || luToProduce <= 0L) {
            return ConversionState.EMPTY;
        }

        long remainingEU = euToConsume;
        for (IEnergyContainer container : energyInputs) {
            if (remainingEU <= 0L) {
                break;
            }
            long drained = container.removeEnergy(remainingEU);
            remainingEU -= drained;
        }

        long actualEUConsumed = euToConsume - remainingEU;
        long actualLUToProduce = (actualEUConsumed * efficiency) / 100L;
        long remainingLU = actualLUToProduce;
        for (ILaserUnitContainer container : laserOutputs) {
            if (remainingLU <= 0L) {
                break;
            }
            long inserted = container.addLaserUnits(remainingLU, false);
            remainingLU -= inserted;
        }

        long actualLUProduced = actualLUToProduce - remainingLU;
        return new ConversionState(actualEUConsumed, actualLUProduced);
    }

    private void clearLastTransfer() {
        if (lastEUConsumed != 0L || lastLUProduced != 0L) {
            lastEUConsumed = 0L;
            lastLUProduced = 0L;
            markDirty();
        }
    }

    private void resetProgress() {
        if (progress != 0) {
            progress = 0;
            markDirty();
        }
    }

    private static IBlockState getHighEnergyCasingState() {
        Block block = Block.getBlockFromName("gregtech:computer_casing");
        return block == null ? net.minecraft.init.Blocks.IRON_BLOCK.getDefaultState() : block.getStateFromMeta(2);
    }

    private static final class ConversionState {
        private static final ConversionState EMPTY = new ConversionState(0L, 0L);

        private final long consumedEU;
        private final long producedLU;

        private ConversionState(long consumedEU, long producedLU) {
            this.consumedEU = consumedEU;
            this.producedLU = producedLU;
        }
    }
}
