package GTInsanityCore.common.metatileentities;

import GTInsanityCore.GTInsanityCore;
import GTInsanityCore.common.recipes.GTIRecipeMaps;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.metatileentities.electric.MetaTileEntityFisher;
import gregtech.common.metatileentities.electric.MetaTileEntitySingleCombustion;
import gregtech.common.metatileentities.electric.MetaTileEntitySingleTurbine;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

public final class GTIMetaTileEntities {

    private static final int CVD_CHAMBER_BASE_ID = 32000;
    private static final int LASER_OUTPUT_HATCH_BASE_ID = 32100;
    private static final int LASER_CONVERSION_ARRAY_ID = 32132;
    private static final int PRIMITIVE_BULK_SMELTER_ID = 32133;
    private static final int ULV_MACHINE_BASE_ID = 32200;

    // Novos IDs para as uniblocks
    private static final int NAQUADAH_REACTOR_BASE_ID = 32300;  // LuV-UHV (5 tiers)
    private static final int MOB_SIMULATOR_BASE_ID = 32310;     // LV-HV (3 tiers)
    private static final int RTG_BASE_ID = 32320;               // EV-UV (4 tiers)
    private static final int VACUUM_CHAMBER_BASE_ID = 32330;    // ULV-HV (5 tiers)
    private static final int FLUIDIZED_BED_REACTOR_BASE_ID = 32340; // LV-IV (4 tiers)

    public static final MetaTileEntityCVD[] CVD_CHAMBERS = new MetaTileEntityCVD[GTValues.OpV + 1];
    public static final MetaTileEntityGTILaserOutputHatch[] LASER_OUTPUT_HATCHES =
            new MetaTileEntityGTILaserOutputHatch[GTValues.OpV + 1];
    public static MetaTileEntityLaserConversionArray LASER_CONVERSION_ARRAY;
    public static MetaTileEntityPrimitiveBulkSmelterMultiblock PRIMITIVE_BULK_SMELTER;

    // Arrays das novas maquinas
    public static SimpleGeneratorMetaTileEntity[] NAQUADAH_REACTORS = new SimpleGeneratorMetaTileEntity[GTValues.OpV + 1];
    public static SimpleMachineMetaTileEntity[] MOB_SIMULATORS = new SimpleMachineMetaTileEntity[GTValues.OpV + 1];
    public static SimpleGeneratorMetaTileEntity[] RTGS = new SimpleGeneratorMetaTileEntity[GTValues.OpV + 1];
    public static SimpleMachineMetaTileEntity[] VACUUM_CHAMBERS = new SimpleMachineMetaTileEntity[GTValues.OpV + 1];
    public static SimpleMachineMetaTileEntity[] FLUIDIZED_BED_REACTORS = new SimpleMachineMetaTileEntity[GTValues.OpV + 1];

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

        registerULVMachines();
        registerNewUniblocks();
    }

    private static void registerULVMachines() {
        if (MetaTileEntities.ELECTRIC_FURNACE[GTValues.ULV] == null) {
            MetaTileEntities.ELECTRIC_FURNACE[GTValues.ULV] = registerSimpleMachine(
                    ULV_MACHINE_BASE_ID,
                    "electric_furnace.ulv",
                    RecipeMaps.FURNACE_RECIPES,
                    Textures.ELECTRIC_FURNACE_OVERLAY,
                    true);
        }
        if (MetaTileEntities.ASSEMBLER[GTValues.ULV] == null) {
            MetaTileEntities.ASSEMBLER[GTValues.ULV] = registerSimpleMachine(
                    ULV_MACHINE_BASE_ID + 1,
                    "assembler.ulv",
                    RecipeMaps.ASSEMBLER_RECIPES,
                    Textures.ASSEMBLER_OVERLAY,
                    true,
                    GTUtility.hvCappedTankSizeFunction);
        }
        if (MetaTileEntities.WIREMILL[GTValues.ULV] == null) {
            MetaTileEntities.WIREMILL[GTValues.ULV] = registerSimpleMachine(
                    ULV_MACHINE_BASE_ID + 2,
                    "wiremill.ulv",
                    RecipeMaps.WIREMILL_RECIPES,
                    Textures.WIREMILL_OVERLAY,
                    true);
        }
        if (MetaTileEntities.ORE_WASHER[GTValues.ULV] == null) {
            MetaTileEntities.ORE_WASHER[GTValues.ULV] = registerSimpleMachine(
                    ULV_MACHINE_BASE_ID + 3,
                    "ore_washer.ulv",
                    RecipeMaps.ORE_WASHER_RECIPES,
                    Textures.ORE_WASHER_OVERLAY,
                    true);
        }
        if (MetaTileEntities.POLARIZER[GTValues.ULV] == null) {
            MetaTileEntities.POLARIZER[GTValues.ULV] = registerSimpleMachine(
                    ULV_MACHINE_BASE_ID + 4,
                    "polarizer.ulv",
                    RecipeMaps.POLARIZER_RECIPES,
                    Textures.POLARIZER_OVERLAY,
                    true);
        }
        if (MetaTileEntities.COMBUSTION_GENERATOR[GTValues.ULV] == null) {
            MetaTileEntities.COMBUSTION_GENERATOR[GTValues.ULV] = registerGenerator(
                    ULV_MACHINE_BASE_ID + 5,
                    new MetaTileEntitySingleCombustion(
                            new ResourceLocation(GTInsanityCore.MODID, "combustion_generator.ulv"),
                            RecipeMaps.COMBUSTION_GENERATOR_FUELS,
                            Textures.COMBUSTION_GENERATOR_OVERLAY,
                            GTValues.ULV,
                            GTUtility.genericGeneratorTankSizeFunction));
        }
        if (MetaTileEntities.STEAM_TURBINE[GTValues.ULV] == null) {
            MetaTileEntities.STEAM_TURBINE[GTValues.ULV] = registerGenerator(
                    ULV_MACHINE_BASE_ID + 6,
                    new MetaTileEntitySingleTurbine(
                            new ResourceLocation(GTInsanityCore.MODID, "steam_turbine.ulv"),
                            RecipeMaps.STEAM_TURBINE_FUELS,
                            Textures.STEAM_TURBINE_OVERLAY,
                            GTValues.ULV,
                            GTUtility.steamGeneratorTankSizeFunction));
        }
        if (MetaTileEntities.FISHER[GTValues.ULV] == null) {
            MetaTileEntities.FISHER[GTValues.ULV] = MetaTileEntities.registerMetaTileEntity(
                    ULV_MACHINE_BASE_ID + 7,
                    new MetaTileEntityFisher(
                            new ResourceLocation(GTInsanityCore.MODID, "fisher.ulv"),
                            GTValues.ULV));
        }
    }

    private static SimpleMachineMetaTileEntity registerSimpleMachine(int id, String name,
                                                                     gregtech.api.recipes.RecipeMap<?> recipeMap,
                                                                     gregtech.client.renderer.ICubeRenderer renderer,
                                                                     boolean frontFacing) {
        return registerSimpleMachine(id, name, recipeMap, renderer, frontFacing, GTUtility.defaultTankSizeFunction);
    }

    private static SimpleMachineMetaTileEntity registerSimpleMachine(int id, String name,
                                                                     gregtech.api.recipes.RecipeMap<?> recipeMap,
                                                                     gregtech.client.renderer.ICubeRenderer renderer,
                                                                     boolean frontFacing,
                                                                     java.util.function.Function<Integer, Integer> tankScaling) {
        return MetaTileEntities.registerMetaTileEntity(
                id,
                new SimpleMachineMetaTileEntity(
                        new ResourceLocation(GTInsanityCore.MODID, name),
                        recipeMap,
                        renderer,
                        GTValues.ULV,
                        frontFacing,
                        tankScaling));
    }

    private static SimpleGeneratorMetaTileEntity registerGenerator(int id, SimpleGeneratorMetaTileEntity generator) {
        return MetaTileEntities.registerMetaTileEntity(id, generator);
    }

    /**
     * Registra as novas uniblocks:
     * - Naquadah Reactor (LuV-UHV)
     * - Mob Simulator (LV-HV)
     * - RTG (EV-UV)
     * - Vacuum Chamber (ULV-HV)
     * - Fluidized Bed Reactor (LV-IV)
     */
    private static void registerNewUniblocks() {
        if (NAQUADAH_REACTORS[GTValues.LuV] != null) {
            return; // Ja registrado
        }

        // ===================================================================
        // Naquadah Reactor (LuV-UHV) - Gerador que usa combustivel de Naquadah
        // ===================================================================
        int id = NAQUADAH_REACTOR_BASE_ID;
        for (int tier = GTValues.LuV; tier <= GTValues.UHV; tier++) {
            String tierName = GTValues.VN[tier].toLowerCase(Locale.ROOT);
            NAQUADAH_REACTORS[tier] = registerGenerator(
                    id++,
                    new MetaTileEntitySingleCombustion(
                            new ResourceLocation(GTInsanityCore.MODID, "naquadah_reactor." + tierName),
                            RecipeMaps.COMBUSTION_GENERATOR_FUELS,
                            Textures.COMBUSTION_GENERATOR_OVERLAY,
                            tier,
                            GTUtility.genericGeneratorTankSizeFunction));
        }

        // ===================================================================
        // Mob Simulator (LV-HV) - Usa recipe map custom
        // ===================================================================
        id = MOB_SIMULATOR_BASE_ID;
        for (int tier = GTValues.LV; tier <= GTValues.HV; tier++) {
            String tierName = GTValues.VN[tier].toLowerCase(Locale.ROOT);
            MOB_SIMULATORS[tier] = registerSimpleMachine(
                    id++,
                    "mob_simulator." + tierName,
                    GTIRecipeMaps.MOB_SIMULATOR_RECIPES,
                    Textures.MASS_FABRICATOR_OVERLAY,
                    true);
        }

        // ===================================================================
        // RTG - Radioisotope Thermoelectric Generator (EV-UV) - Gerador passivo
        // ===================================================================
        id = RTG_BASE_ID;
        for (int tier = GTValues.EV; tier <= GTValues.UV; tier++) {
            String tierName = GTValues.VN[tier].toLowerCase(Locale.ROOT);
            RTGS[tier] = registerGenerator(
                    id++,
                    new MetaTileEntitySingleCombustion(
                            new ResourceLocation(GTInsanityCore.MODID, "rtg." + tierName),
                            RecipeMaps.COMBUSTION_GENERATOR_FUELS,
                            Textures.POWER_SUBSTATION_OVERLAY,
                            tier,
                            GTUtility.genericGeneratorTankSizeFunction));
        }

        // ===================================================================
        // Vacuum Chamber (ULV-HV)
        // ===================================================================
        id = VACUUM_CHAMBER_BASE_ID;
        for (int tier = GTValues.ULV; tier <= GTValues.HV; tier++) {
            String tierName = GTValues.VN[tier].toLowerCase(Locale.ROOT);
            VACUUM_CHAMBERS[tier] = registerSimpleMachine(
                    id++,
                    "vacuum_chamber." + tierName,
                    RecipeMaps.VACUUM_RECIPES,
                    Textures.VACUUM_FREEZER_OVERLAY,
                    true);
        }

        // ===================================================================
        // Fluidized Bed Reactor (LV-IV)
        // ===================================================================
        id = FLUIDIZED_BED_REACTOR_BASE_ID;
        for (int tier = GTValues.LV; tier <= GTValues.IV; tier++) {
            String tierName = GTValues.VN[tier].toLowerCase(Locale.ROOT);
            FLUIDIZED_BED_REACTORS[tier] = registerSimpleMachine(
                    id++,
                    "fluidized_bed_reactor." + tierName,
                    RecipeMaps.CHEMICAL_BATH_RECIPES,
                    Textures.CHEMICAL_BATH_OVERLAY,
                    true);
        }
    }
}
