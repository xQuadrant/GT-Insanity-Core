package GTInsanityCore.common.metatileentities;

import GTInsanityCore.API.capability.ILaserUnitContainer;
import GTInsanityCore.common.laser.LaserUnitStorage;
import GTInsanityCore.common.laser.LaserUnitTiers;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.IDataInfoProvider;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.Collections;
import java.util.List;

public class MetaTileEntityGTILaserOutputHatch extends MetaTileEntityMultiblockPart
        implements IMultiblockAbilityPart<ILaserUnitContainer>, IDataInfoProvider, ILaserUnitContainer {

    private static final String NBT_LASER_UNITS = "LaserUnits";

    private final int tier;
    private final LaserUnitStorage storage;

    public MetaTileEntityGTILaserOutputHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.tier = tier;
        this.storage = new LaserUnitStorage(
                LaserUnitTiers.bufferForTier(tier),
                LaserUnitTiers.transferRate(tier),
                LaserUnitTiers.transferRate(tier));
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityGTILaserOutputHatch(metaTileEntityId, tier);
    }

    @Override
    public MultiblockAbility<ILaserUnitContainer> getAbility() {
        return GTIMultiblockAbilities.LASER_OUTPUT_LU;
    }

    @Override
    public void registerAbilities(List<ILaserUnitContainer> abilityList) {
        abilityList.add(this);
    }

    @Override
    public List<ITextComponent> getDataInfo() {
        return Collections.singletonList(new TextComponentString(
                "LU: " + getStoredLaserUnits() + " / " + getLaserUnitCapacity()));
    }

    @Override
    public void renderMetaTileEntity(codechicken.lib.render.CCRenderState renderState,
                                     codechicken.lib.vec.Matrix4 translation,
                                     codechicken.lib.render.pipeline.IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.LASER_SOURCE.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public ICubeRenderer getBaseTexture() {
        return Textures.ADVANCED_COMPUTER_CASING;
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
    public long addLaserUnits(long amount, boolean simulate) {
        long inserted = storage.receive(amount, simulate);
        if (!simulate && inserted > 0L) {
            markDirty();
        }
        return inserted;
    }

    @Override
    public long removeLaserUnits(long amount, boolean simulate) {
        long extracted = storage.extract(amount, simulate);
        if (!simulate && extracted > 0L) {
            markDirty();
        }
        return extracted;
    }

    @Override
    public long getMaxLaserTransfer() {
        return LaserUnitTiers.transferRate(tier);
    }

    @Override
    public int getLaserTier() {
        return tier;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        storage.writeToNBT(data, NBT_LASER_UNITS);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        storage.readFromNBT(data, NBT_LASER_UNITS);
    }

    @Override
    public boolean isValidFrontFacing(EnumFacing facing) {
        return facing != EnumFacing.UP && facing != EnumFacing.DOWN;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }
}
