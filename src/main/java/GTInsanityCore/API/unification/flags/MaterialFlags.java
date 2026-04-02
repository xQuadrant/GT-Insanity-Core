package GTInsanityCore.API.unification.flags;

import gregtech.api.unification.material.flags.MaterialFlag;

public class InsanityMaterialFlags {

    // Particle physics flags
    public static MaterialFlag FLAG_COLOR_CONFINEMENT;
    public static MaterialFlag FLAG_WEAK_DECAY;
    public static MaterialFlag FLAG_REQUIRES_CONTAINMENT;
    public static MaterialFlag FLAG_INSTABLE_HALFLIFE;
    public static MaterialFlag FLAG_META_STABLE;

    // Standard GTCEu flags we need to reference
    public static MaterialFlag GENERATE_PLASMA;
    public static MaterialFlag GENERATE_INGOT;
    public static MaterialFlag GENERATE_DUST;
    public static MaterialFlag GENERATE_GAS;
    public static MaterialFlag DISABLE_DECOMPOSITION;

    public static void init() {
        // Register custom flags
        FLAG_COLOR_CONFINEMENT = new MaterialFlag("color_confinement");
        FLAG_WEAK_DECAY = new MaterialFlag("weak_decay");
        FLAG_REQUIRES_CONTAINMENT = new MaterialFlag("requires_containment");
        FLAG_INSTABLE_HALFLIFE = new MaterialFlag("instable_halflife");
        FLAG_META_STABLE = new MaterialFlag("meta_stable");

        // Reference existing GTCEu flags (or create if they don't exist)
        // These are already in GTCEu, just reference them
        GENERATE_PLASMA = MaterialFlag.getByName("generate_plasma");
        GENERATE_INGOT = MaterialFlag.getByName("generate_ingot");
        GENERATE_DUST = MaterialFlag.getByName("generate_dust");
        GENERATE_GAS = MaterialFlag.getByName("generate_gas");
        DISABLE_DECOMPOSITION = MaterialFlag.getByName("disable_decomposition");
    }
}