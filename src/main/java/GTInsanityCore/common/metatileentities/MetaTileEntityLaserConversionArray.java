package GTInsanityCore.common.metatileentities;

import GTInsanityCore.API.capability.ILaserUnitContainer;
import GTInsanityCore.common.recipes.GTILaserConversionRecipes;
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

    private static final int MAX_PROGRESS = 20; // 1 segundo = 20 ticks

    private static final String NBT_WORKING_ENABLED = "WorkingEnabled";
    private static final String NBT_ACTIVE = "Active";
    private static final String NBT_PROGRESS = "Progress";
    private static final String NBT_COMPLETED_CYCLES = "CompletedCycles";
    private static final String NBT_LAST_EU_CONSUMED = "LastEUConsumed";
    private static final String NBT_LAST_LU_PRODUCED = "LastLUProduced";

    private boolean workingEnabled = true;
    private boolean active = false;
    private int progress = 0;
    private int completedCycles = 0;
    private long lastEUConsumed = 0L;
    private long lastLUProduced = 0L;

    private int operatingTier = GTValues.LV;
    private GTILaserConversionRecipes.LaserConversionData conversionData = null;

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
                .aisle("XXX", "XXX", "XXX")
                .aisle("XXX", "X#X", "XXX")
                .aisle("XXX", "XSX", "XXX")
                .where('S', selfPredicate())
                .where('X', states(getHighEnergyCasingState()).setMinGlobalLimited(20)
                        .or(abilities(MultiblockAbility.INPUT_ENERGY)
                                .setMinGlobalLimited(1).setMaxGlobalLimited(1).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH)
                                .setExactLimit(1).setPreviewCount(1))
                        .or(abilities(GTIMultiblockAbilities.LASER_OUTPUT_LU)
                                .setMinGlobalLimited(1).setMaxGlobalLimited(3).setPreviewCount(3)))
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
            shapes.add(createMatchingShape(hatch, MetaTileEntities.ENERGY_INPUT_HATCH[tier], 1));
            shapes.add(createMatchingShape(hatch, MetaTileEntities.ENERGY_INPUT_HATCH[tier], 2));
            shapes.add(createMatchingShape(hatch, MetaTileEntities.ENERGY_INPUT_HATCH[tier], 3));
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

        // Atualizar operating tier e dados de conversao
        operatingTier = getOperatingTier();
        conversionData = GTILaserConversionRecipes.getConversionData(operatingTier);

        if (conversionData == null) {
            active = false;
            resetProgress();
            clearLastTransfer();
            return;
        }

        // Verificar se ha energia suficiente
        List<IEnergyContainer> energyInputs = getAbilities(MultiblockAbility.INPUT_ENERGY);
        if (energyInputs.isEmpty()) {
            active = false;
            resetProgress();
            clearLastTransfer();
            return;
        }

        long euRequired = conversionData.euPerTick;
        long totalStoredEU = 0L;
        for (IEnergyContainer container : energyInputs) {
            totalStoredEU += Math.max(0L, container.getEnergyStored());
        }

        if (totalStoredEU < euRequired) {
            // Nao ha energia suficiente
            if (progress > 0) {
                // Regredir progresso
                progress = Math.max(0, progress - 1);
            }
            active = false;
            return;
        }

        // Consumir energia
        long euConsumed = 0L;
        long euRemaining = euRequired;
        for (IEnergyContainer container : energyInputs) {
            if (euRemaining <= 0L) break;
            long canDrain = Math.min(euRemaining, container.getEnergyStored());
            long drained = container.removeEnergy(canDrain);
            euRemaining -= drained;
            euConsumed += drained;
        }

        if (euConsumed < euRequired) {
            // Nao conseguiu consumir toda energia necessaria
            active = false;
            if (progress > 0) {
                progress = Math.max(0, progress - 1);
            }
            return;
        }

        // Avancar progresso
        active = true;
        progress++;

        if (progress >= MAX_PROGRESS) {
            // Ciclo completado - produzir LU
            long luProduced = produceLaserUnits();
            lastEUConsumed = euRequired * MAX_PROGRESS;
            lastLUProduced = luProduced;
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
        conversionData = null;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        int efficiency = conversionData != null ? conversionData.efficiencyPercent : 0;
        MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(active, workingEnabled)
                .addCustom(lines -> {
                    if (!isStructureFormed()) {
                        return;
                    }
                    lines.add(new TextComponentString("Operating Tier: " + GTValues.VNF[operatingTier]));
                    lines.add(new TextComponentString("Efficiency: " + efficiency + "%"));
                    lines.add(new TextComponentString("EU/t Required: " + (conversionData != null ? conversionData.euPerTick : 0)));
                    lines.add(new TextComponentString("EU -> LU: " + lastEUConsumed + " EU -> " + lastLUProduced + " LU"));
                    lines.add(new TextComponentString("Laser Output Capacity: " + getLaserCapacity() + " LU/t"));
                    lines.add(new TextComponentString("Laser Hatches: " + getAbilities(GTIMultiblockAbilities.LASER_OUTPUT_LU).size()));
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

    private long getLaserCapacity() {
        long total = 0L;
        for (ILaserUnitContainer container : getAbilities(GTIMultiblockAbilities.LASER_OUTPUT_LU)) {
            total += container.getMaxLaserTransfer();
        }
        return total;
    }

    private int getOperatingTier() {
        int tier = GTValues.LV;
        boolean foundAny = false;

        // Determinar tier baseado no energy hatch
        for (IEnergyContainer container : getAbilities(MultiblockAbility.INPUT_ENERGY)) {
            int hatchTier = gregtech.api.util.GTUtility.getTierByVoltage(container.getInputVoltage());
            tier = foundAny ? Math.min(tier, hatchTier) : hatchTier;
            foundAny = true;
        }

        // Limitar pelo tier do laser output hatch
        for (ILaserUnitContainer container : getAbilities(GTIMultiblockAbilities.LASER_OUTPUT_LU)) {
            tier = foundAny ? Math.min(tier, container.getLaserTier()) : container.getLaserTier();
            foundAny = true;
        }

        return foundAny ? tier : GTValues.LV;
    }

    /**
     * Produz Laser Units nos output hatches baseado nos dados de conversao.
     */
    private long produceLaserUnits() {
        if (conversionData == null) {
            return 0L;
        }

        List<ILaserUnitContainer> laserOutputs = getAbilities(GTIMultiblockAbilities.LASER_OUTPUT_LU);
        if (laserOutputs.isEmpty()) {
            return 0L;
        }

        long luToProduce = conversionData.luPerCycle;
        if (luToProduce <= 0L) {
            return 0L;
        }

        // Distribuir LU entre os output hatches
        long totalProduced = 0L;
        long remainingLU = luToProduce;

        for (ILaserUnitContainer container : laserOutputs) {
            if (remainingLU <= 0L) {
                break;
            }
            long inserted = container.addLaserUnits(remainingLU, false);
            remainingLU -= inserted;
            totalProduced += inserted;
        }

        return totalProduced;
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

    private MultiblockShapeInfo createMatchingShape(MetaTileEntityGTILaserOutputHatch hatch,
                                                    MetaTileEntity energyHatch, int outputCount) {
        String frontTop = outputCount >= 2 ? "XOX" : "XMX";
        String frontMiddle = outputCount >= 1 ? "ESO" : "ESX";
        String frontBottom = outputCount >= 3 ? "XOX" : "XXX";

        return MultiblockShapeInfo.builder()
                .aisle("XXX", "XXX", "XXX")
                .aisle("XXX", "X#X", "XXX")
                .aisle(frontTop, frontMiddle, frontBottom)
                .where('S', this, EnumFacing.NORTH)
                .where('X', getHighEnergyCasingState())
                .where('#', net.minecraft.init.Blocks.AIR.getDefaultState())
                .where('E', energyHatch, EnumFacing.NORTH)
                .where('M', MetaTileEntities.MAINTENANCE_HATCH, EnumFacing.NORTH)
                .where('O', hatch, EnumFacing.NORTH)
                .build();
    }
}
