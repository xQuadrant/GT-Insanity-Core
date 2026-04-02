package GTInsanityCore.common.metatileentities;

import GTInsanityCore.API.capability.ILaserUnitContainer;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class MetaTileEntityGTILaserOutputHatch extends MetaTileEntityMultiblockPart
        implements IMultiblockAbilityPart<ILaserUnitContainer>, IDataInfoProvider, ILaserUnitContainer {

    private static final String NBT_TRANSFERRED_THIS_TICK = "TransferredThisTick";
    private static final String NBT_LAST_TRANSFERRED = "LastTransferred";
    private static final String NBT_LAST_TRANSFER_TICK = "LastTransferTick";

    private final int tier;
    private long transferredThisTick;
    private long lastTransferred;
    private long lastTransferTick = -1L;

    public MetaTileEntityGTILaserOutputHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.tier = tier;
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
                getTransferredThisTick() + "/" + getMaxLaserTransfer() + " LU/t"));
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
        return 0L;
    }

    @Override
    public long getLaserUnitCapacity() {
        return 0L;
    }

    @Override
    public long addLaserUnits(long amount, boolean simulate) {
        resetTransferWindowIfNeeded();
        long inserted = Math.min(Math.max(0L, amount), getMaxLaserTransfer() - transferredThisTick);
        if (!simulate && inserted > 0L) {
            transferredThisTick += inserted;
            lastTransferred = transferredThisTick;
            markDirty();
        }
        return inserted;
    }

    @Override
    public long removeLaserUnits(long amount, boolean simulate) {
        return 0L;
    }

    @Override
    public long getMaxLaserTransfer() {
        return LaserUnitTiers.transferRate(tier);
    }

    @Override
    public long getTransferredThisTick() {
        resetTransferWindowIfNeeded();
        return transferredThisTick;
    }

    @Override
    public int getLaserTier() {
        return tier;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setLong(NBT_TRANSFERRED_THIS_TICK, transferredThisTick);
        data.setLong(NBT_LAST_TRANSFERRED, lastTransferred);
        data.setLong(NBT_LAST_TRANSFER_TICK, lastTransferTick);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        transferredThisTick = data.getLong(NBT_TRANSFERRED_THIS_TICK);
        lastTransferred = data.getLong(NBT_LAST_TRANSFERRED);
        lastTransferTick = data.getLong(NBT_LAST_TRANSFER_TICK);
    }

    @Override
    public boolean isValidFrontFacing(EnumFacing facing) {
        return facing != EnumFacing.UP && facing != EnumFacing.DOWN;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public void update() {
        super.update();
        resetTransferWindowIfNeeded();
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        tooltip.add("Transfering lasers from distance.");
        tooltip.add(TextFormatting.RED + "Laser Beams must be transmitted in a straight line.");
        tooltip.add(TextFormatting.GREEN + "Outputs up to: " + TextFormatting.WHITE + getMaxLaserTransfer() + " LU/t");
    }

    private void resetTransferWindowIfNeeded() {
        if (getWorld() == null) {
            return;
        }
        long worldTime = getWorld().getTotalWorldTime();
        if (lastTransferTick != worldTime) {
            transferredThisTick = 0L;
            lastTransferTick = worldTime;
        }
    }
}
