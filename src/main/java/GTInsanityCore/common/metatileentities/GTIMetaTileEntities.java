package GTInsanityCore.common.metatileentities;

import GTInsanityCore.GTInsanityCore;
import gregtech.api.GTValues;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

public final class GTIMetaTileEntities {

    private static final int CVD_CHAMBER_BASE_ID = 32000;
    private static final int LASER_INPUT_HATCH_BASE_ID = 32100;
    private static final int SIMPLE_LASER_ARRAY_ID = 32132;
    private static final int PRIMITIVE_BULK_SMELTER_ID = 32133;

    public static final MetaTileEntityCVD[] CVD_CHAMBERS = new MetaTileEntityCVD[GTValues.OpV + 1];
    public static final MetaTileEntityGTILaserHatch[] LASER_INPUT_HATCHES =
            new MetaTileEntityGTILaserHatch[GTValues.OpV + 1];
    public static MetaTileEntitySimpleLaserArray SIMPLE_LASER_ARRAY;
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

        id = LASER_INPUT_HATCH_BASE_ID;
        for (int tier = GTValues.LV; tier <= GTValues.OpV; tier++) {
            String tierName = GTValues.VN[tier].toLowerCase(Locale.ROOT);
            LASER_INPUT_HATCHES[tier] = MetaTileEntities.registerMetaTileEntity(
                    id++,
                    new MetaTileEntityGTILaserHatch(
                            new ResourceLocation(GTInsanityCore.MODID, "laser_input_hatch." + tierName),
                            false,
                            tier,
                            1));
        }

        SIMPLE_LASER_ARRAY = MetaTileEntities.registerMetaTileEntity(
                SIMPLE_LASER_ARRAY_ID,
                new MetaTileEntitySimpleLaserArray(new ResourceLocation(GTInsanityCore.MODID, "simple_laser_array")));

        PRIMITIVE_BULK_SMELTER = MetaTileEntities.registerMetaTileEntity(
                PRIMITIVE_BULK_SMELTER_ID,
                new MetaTileEntityPrimitiveBulkSmelterMultiblock(
                        new ResourceLocation(GTInsanityCore.MODID, "primitive_bulk_smelter")));
    }
}
