package GTInsanityCore.API.unification;

import GTInsanityCore.GTInsanityCore;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconSet;
import net.minecraft.util.ResourceLocation;

/**
 * Registro de fluidos industriais customizados do GTInsanityCore.
 * Inclui aguas contaminadas, agua do mar e vapores de alta temperatura.
 */
public final class GTIFluids {

    // Fluidos de agua
    public static Material SeaWater;
    public static Material ContaminatedSeaWater;
    public static Material ContaminatedWater;

    // Fluidos de vapor
    public static Material SuperheatedSteam;
    public static Material SupercriticalSteam;

    private static boolean initialized = false;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        // ===================================================================
        // Fluidos de agua
        // ===================================================================

        // Agua do mar - liquido azul-esverdeado
        SeaWater = new Material.Builder(32200, new ResourceLocation(GTInsanityCore.MODID, "sea_water"))
                .liquid()
                .color(0x1E90FF)
                .iconSet(MaterialIconSet.FLUID)
                .build();

        // Agua do mar contaminada - liquido verde escuro
        ContaminatedSeaWater = new Material.Builder(32201, new ResourceLocation(GTInsanityCore.MODID, "contaminated_sea_water"))
                .liquid()
                .color(0x2F4F2F)
                .iconSet(MaterialIconSet.FLUID)
                .build();

        // Agua contaminada - liquido marrom-esverdeado
        ContaminatedWater = new Material.Builder(32202, new ResourceLocation(GTInsanityCore.MODID, "contaminated_water"))
                .liquid()
                .color(0x556B2F)
                .iconSet(MaterialIconSet.FLUID)
                .build();

        // ===================================================================
        // Fluidos de vapor
        // ===================================================================

        // Vapor superaquecido - gas branco-amarelado (acima de 800C)
        SuperheatedSteam = new Material.Builder(32203, new ResourceLocation(GTInsanityCore.MODID, "superheated_steam"))
                .gas()
                .color(0xFFFACD)
                .iconSet(MaterialIconSet.FLUID)
                .build();

        // Vapor supercritico - gas branco-azulado (estado supercritico)
        SupercriticalSteam = new Material.Builder(32204, new ResourceLocation(GTInsanityCore.MODID, "supercritical_steam"))
                .gas()
                .color(0xE0FFFF)
                .iconSet(MaterialIconSet.FLUID)
                .build();
    }
}
