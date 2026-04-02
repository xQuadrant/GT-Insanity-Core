package GTInsanityCore.API.unification.flags;

import gregtech.api.unification.material.info.MaterialFlag;

public final class MaterialFlags {

    private MaterialFlags() {
    }

    // Particle category flags
    public static final MaterialFlag PARTICLE = new MaterialFlag.Builder("particle").build();
    public static final MaterialFlag QUARK = new MaterialFlag.Builder("quark")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag LEPTON = new MaterialFlag.Builder("lepton")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag GAUGE_BOSON = new MaterialFlag.Builder("gauge_boson")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag SCALAR_BOSON = new MaterialFlag.Builder("scalar_boson")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag ANTIMATTER = new MaterialFlag.Builder("antimatter")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag NEUTRINO = new MaterialFlag.Builder("neutrino")
            .requireFlags(LEPTON)
            .build();

    // Particle behavior flags
    public static final MaterialFlag COLOR_CONFINEMENT = new MaterialFlag.Builder("color_confinement")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag WEAK_DECAY = new MaterialFlag.Builder("weak_decay")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag REQUIRES_CONTAINMENT = new MaterialFlag.Builder("requires_containment")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag INSTABLE_HALFLIFE = new MaterialFlag.Builder("instable_halflife")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag META_STABLE = new MaterialFlag.Builder("meta_stable")
            .requireFlags(PARTICLE)
            .build();

    // Particle material generation/state flags
    public static final MaterialFlag GENERATE_PLASMA = new MaterialFlag.Builder("generate_plasma")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag GENERATE_INGOT = new MaterialFlag.Builder("generate_ingot")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag GENERATE_DUST = new MaterialFlag.Builder("generate_dust")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag GENERATE_GAS = new MaterialFlag.Builder("generate_gas")
            .requireFlags(PARTICLE)
            .build();
    public static final MaterialFlag DISABLE_DECOMPOSITION =
            gregtech.api.unification.material.info.MaterialFlags.DISABLE_DECOMPOSITION;
}
