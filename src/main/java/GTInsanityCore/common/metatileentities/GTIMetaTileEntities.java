package GTInsanityCore.common.metatileentities;

import GTInsanityCore.GTInsanityCore;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

public final class GTIMetaTileEntities {

    private static final int CVD_CHAMBER_BASE_ID = 32000;
    private static final int LASER_OUTPUT_HATCH_BASE_ID = 32100;
    private static final int LASER_CONVERSION_ARRAY_ID = 32132;
    private static final int PRIMITIVE_BULK_SMELTER_ID = 32133;

    public static final MetaTileEntityCVD[] CVD_CHAMBERS = new MetaTileEntityCVD[GTValues.OpV + 1];
    public static final MetaTileEntityGTILaserOutputHatch[] LASER_OUTPUT_HATCHES =
            new MetaTileEntityGTILaserOutputHatch[GTValues.OpV + 1];
    public static MetaTileEntityLaserConversionArray LASER_CONVERSION_ARRAY;
    public static MetaTileEntityPrimitiveBulkSmelterMultiblock PRIMITIVE_BULK_SMELTER;

    private GTIMetaTileEntities() {
    }

    public static void init() {
        if (CVD_CHAMBERS[GTValues.LV] != null) {
            return;
        }

        int id = CVD_CHAMBER_BASE_ID;
        for (int tier = GTValues.LV; tier <= GTValues.OpV; tier++) {
            String tierName = GTValues.VN[tier].toLowerCase(Locale.ROOT);
            CVD_CHAMBERS[tier] = MetaTileEntities.registerMetaTileEntity(
                    id++,
                    new MetaTileEntityCVD(
                            new ResourceLocation(GTInsanityCore.MODID, "cvd_chamber." + tierName),
                            tier));
        }

        id = LASER_OUTPUT_HATCH_BASE_ID;
        for (int tier = GTValues.LV; tier <= GTValues.OpV; tier++) {
            String tierName = GTValues.VN[tier].toLowerCase(Locale.ROOT);
            LASER_OUTPUT_HATCHES[tier] = MetaTileEntities.registerMetaTileEntity(
                    id++,
                    new MetaTileEntityGTILaserOutputHatch(
                            new ResourceLocation(GTInsanityCore.MODID, "laser_output_hatch." + tierName),
                            tier));
            MultiblockAbility.registerMultiblockAbility(
                    GTIMultiblockAbilities.LASER_OUTPUT_LU,
                    LASER_OUTPUT_HATCHES[tier]);
        }

        LASER_CONVERSION_ARRAY = MetaTileEntities.registerMetaTileEntity(
                LASER_CONVERSION_ARRAY_ID,
                new MetaTileEntityLaserConversionArray(
                        new ResourceLocation(GTInsanityCore.MODID, "laser_conversion_array")));

        PRIMITIVE_BULK_SMELTER = MetaTileEntities.registerMetaTileEntity(
                PRIMITIVE_BULK_SMELTER_ID,
                new MetaTileEntityPrimitiveBulkSmelterMultiblock(
                        new ResourceLocation(GTInsanityCore.MODID, "primitive_bulk_smelter")));
    }
}
