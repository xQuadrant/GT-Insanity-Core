package GTInsanityCore.common.metatileentities;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityLaserHatch;
import net.minecraft.util.ResourceLocation;

public class MetaTileEntityGTILaserHatch extends MetaTileEntityLaserHatch {

    private final boolean isOutput;
    private final int tier;
    private final int amperage;

    public MetaTileEntityGTILaserHatch(ResourceLocation metaTileEntityId, boolean isOutput, int tier, int amperage) {
        super(metaTileEntityId, isOutput, tier, amperage);
        this.isOutput = isOutput;
        this.tier = tier;
        this.amperage = amperage;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityGTILaserHatch(metaTileEntityId, isOutput, tier, amperage);
    }
}
