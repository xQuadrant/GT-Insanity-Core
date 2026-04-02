package GTInsanityCore.API.unification;

import GTInsanityCore.API.unification.flags.MaterialFlags;
import GTInsanityCore.API.unification.materials.IParticleMaterial;
import GTInsanityCore.API.unification.materials.InsanityMaterialRegistry;
import GTInsanityCore.API.unification.materials.ParticleMaterial;
import GTInsanityCore.API.unification.materials.ParticleMaterial.ParticleType;
import GTInsanityCore.GTInsanityCore;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconSet;
import net.minecraft.util.ResourceLocation;

public class GTIMaterials {

    public static IParticleMaterial UpQuark;
    public static IParticleMaterial DownQuark;
    public static IParticleMaterial CharmQuark;
    public static IParticleMaterial StrangeQuark;
    public static IParticleMaterial TopQuark;
    public static IParticleMaterial BottomQuark;

    public static IParticleMaterial Electron;
    public static IParticleMaterial Muon;
    public static IParticleMaterial Tau;
    public static IParticleMaterial ElectronNeutrino;
    public static IParticleMaterial MuonNeutrino;
    public static IParticleMaterial TauNeutrino;

    public static IParticleMaterial Photon;
    public static IParticleMaterial Gluon;
    public static IParticleMaterial WBosonPlus;
    public static IParticleMaterial WBosonMinus;
    public static IParticleMaterial ZBoson;
    public static IParticleMaterial HiggsBoson;

    public static IParticleMaterial Positron;
    public static IParticleMaterial AntiUpQuark;
    public static IParticleMaterial AntiDownQuark;

    private static boolean initialized = false;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        UpQuark = createQuark("up_quark", 32100, 0xFF6B6B, 2.2, 0.667, 0.5);
        DownQuark = createQuark("down_quark", 32101, 0x4ECDC4, 4.7, -0.333, 0.5);
        CharmQuark = createQuark("charm_quark", 32102, 0xF7B267, 1275, 0.667, 0.5);
        StrangeQuark = createQuark("strange_quark", 32103, 0x7BD389, 95, -0.333, 0.5);
        TopQuark = createHeavyQuark("top_quark", 32104, 0xC06CFF, 173000, 0.667, 0.5, 20 * 5);
        BottomQuark = createQuark("bottom_quark", 32105, 0x3D5A80, 4180, -0.333, 0.5);

        Electron = createLepton("electron", 32110, 0x63ADF2, 0.511, -1.0, 0.5, true);
        Muon = createUnstableLepton("muon", 32111, 0x3A86FF, 105.7, -1.0, 0.5, 20 * 120);
        Tau = createUnstableLepton("tau", 32112, 0x8338EC, 1777, -1.0, 0.5, 20 * 300);

        ElectronNeutrino = createNeutrino("electron_neutrino", 32113, 0xD9ED92, 0.0000022, 0, 0.5);
        MuonNeutrino = createNeutrino("muon_neutrino", 32114, 0xB5E48C, 0.17, 0, 0.5);
        TauNeutrino = createNeutrino("tau_neutrino", 32115, 0x76C893, 15.5, 0, 0.5);

        Photon = createGaugeBoson("photon", 32120, 0xFFF3B0, 0, 0, 1.0);
        Gluon = createGluon("gluon", 32121, 0xFF7F50, 0, 0, 1.0);
        WBosonPlus = createUnstableBoson("w_boson_plus", 32122, 0x90E0EF, 80400, 1.0, 1.0, 20 * 10);
        WBosonMinus = createUnstableBoson("w_boson_minus", 32123, 0x48CAE4, 80400, -1.0, 1.0, 20 * 10);
        ZBoson = createUnstableBoson("z_boson", 32124, 0xADE8F4, 91200, 0, 1.0, 20 * 15);
        HiggsBoson = createScalarBoson("higgs_boson", 32125, 0xFFB4A2, 125100, 0, 0);

        Positron = createAntimatterLepton("positron", 32130, 0xFF99C8, 0.511, 1.0, 0.5);
        AntiUpQuark = createAntimatterQuark("anti_up_quark", 32131, 0xFF8FA3, 2.2, -0.667, 0.5);
        AntiDownQuark = createAntimatterQuark("anti_down_quark", 32132, 0xFB6F92, 4.7, 0.333, 0.5);

        InsanityMaterialRegistry.validateIDs();
    }

    private static IParticleMaterial createQuark(String name, int id, int color, double mass, double charge, double spin) {
        return buildParticleMaterial(name, id, color, ParticleType.QUARK, mass, charge, spin, -1,
                MaterialIconSet.METALLIC, new BuilderConfigurer() {
                    @Override
                    public void configure(Material.Builder builder) {
                        builder.plasma();
                    }
                },
                MaterialFlags.PARTICLE,
                MaterialFlags.QUARK,
                MaterialFlags.COLOR_CONFINEMENT,
                MaterialFlags.REQUIRES_CONTAINMENT,
                MaterialFlags.GENERATE_PLASMA,
                MaterialFlags.DISABLE_DECOMPOSITION);
    }

    private static IParticleMaterial createHeavyQuark(String name, int id, int color, double mass, double charge,
                                                      double spin, int halfLife) {
        IParticleMaterial material = createQuark(name, id, color, mass, charge, spin);
        material.setHalfLife(halfLife);
        return material;
    }

    private static IParticleMaterial createLepton(String name, int id, int color, double mass, double charge,
                                                  double spin, final boolean canContain) {
        return buildParticleMaterial(name, id, color, ParticleType.LEPTON, mass, charge, spin, -1,
                MaterialIconSet.SHINY, new BuilderConfigurer() {
                    @Override
                    public void configure(Material.Builder builder) {
                        if (canContain) {
                            builder.ingot().dust();
                        }
                    }
                },
                canContain
                        ? new gregtech.api.unification.material.info.MaterialFlag[]{
                        MaterialFlags.PARTICLE,
                        MaterialFlags.LEPTON,
                        MaterialFlags.GENERATE_INGOT,
                        MaterialFlags.GENERATE_DUST
                }
                        : new gregtech.api.unification.material.info.MaterialFlag[]{
                        MaterialFlags.PARTICLE,
                        MaterialFlags.LEPTON
                });
    }

    private static IParticleMaterial createUnstableLepton(String name, int id, int color, double mass, double charge,
                                                          double spin, int halfLife) {
        IParticleMaterial material = createLepton(name, id, color, mass, charge, spin, true);
        material.setHalfLife(halfLife);
        material.addFlag(MaterialFlags.WEAK_DECAY);
        return material;
    }

    private static IParticleMaterial createNeutrino(String name, int id, int color, double mass, double charge,
                                                    double spin) {
        return buildParticleMaterial(name, id, color, ParticleType.LEPTON, mass, charge, spin, -1,
                MaterialIconSet.FINE, new BuilderConfigurer() {
                    @Override
                    public void configure(Material.Builder builder) {
                        builder.gas();
                    }
                },
                MaterialFlags.PARTICLE,
                MaterialFlags.LEPTON,
                MaterialFlags.NEUTRINO,
                MaterialFlags.GENERATE_GAS,
                MaterialFlags.DISABLE_DECOMPOSITION);
    }

    private static IParticleMaterial createGaugeBoson(String name, int id, int color, double mass, double charge,
                                                      double spin) {
        return buildParticleMaterial(name, id, color, ParticleType.GAUGE_BOSON, mass, charge, spin, -1,
                MaterialIconSet.BRIGHT, new BuilderConfigurer() {
                    @Override
                    public void configure(Material.Builder builder) {
                        builder.gas();
                    }
                },
                MaterialFlags.PARTICLE,
                MaterialFlags.GAUGE_BOSON,
                MaterialFlags.GENERATE_GAS);
    }

    private static IParticleMaterial createGluon(String name, int id, int color, double mass, double charge,
                                                 double spin) {
        return buildParticleMaterial(name, id, color, ParticleType.GAUGE_BOSON, mass, charge, spin, -1,
                MaterialIconSet.BRIGHT, new BuilderConfigurer() {
                    @Override
                    public void configure(Material.Builder builder) {
                        builder.plasma();
                    }
                },
                MaterialFlags.PARTICLE,
                MaterialFlags.GAUGE_BOSON,
                MaterialFlags.COLOR_CONFINEMENT,
                MaterialFlags.GENERATE_PLASMA,
                MaterialFlags.DISABLE_DECOMPOSITION);
    }

    private static IParticleMaterial createUnstableBoson(String name, int id, int color, double mass, double charge,
                                                         double spin, int halfLife) {
        return buildParticleMaterial(name, id, color, ParticleType.GAUGE_BOSON, mass, charge, spin, halfLife,
                MaterialIconSet.BRIGHT, new BuilderConfigurer() {
                    @Override
                    public void configure(Material.Builder builder) {
                        builder.plasma();
                    }
                },
                MaterialFlags.PARTICLE,
                MaterialFlags.GAUGE_BOSON,
                MaterialFlags.GENERATE_PLASMA,
                MaterialFlags.INSTABLE_HALFLIFE);
    }

    private static IParticleMaterial createScalarBoson(String name, int id, int color, double mass, double charge,
                                                       double spin) {
        return buildParticleMaterial(name, id, color, ParticleType.SCALAR_BOSON, mass, charge, spin, 20 * 20,
                MaterialIconSet.SHINY, new BuilderConfigurer() {
                    @Override
                    public void configure(Material.Builder builder) {
                        builder.plasma();
                    }
                },
                MaterialFlags.PARTICLE,
                MaterialFlags.SCALAR_BOSON,
                MaterialFlags.GENERATE_PLASMA,
                MaterialFlags.INSTABLE_HALFLIFE,
                MaterialFlags.DISABLE_DECOMPOSITION);
    }

    private static IParticleMaterial createAntimatterQuark(String name, int id, int color, double mass, double charge,
                                                           double spin) {
        return buildParticleMaterial(name, id, color, ParticleType.ANTI_QUARK, mass, charge, spin, -1,
                MaterialIconSet.METALLIC, new BuilderConfigurer() {
                    @Override
                    public void configure(Material.Builder builder) {
                        builder.plasma();
                    }
                },
                MaterialFlags.PARTICLE,
                MaterialFlags.QUARK,
                MaterialFlags.ANTIMATTER,
                MaterialFlags.COLOR_CONFINEMENT,
                MaterialFlags.REQUIRES_CONTAINMENT,
                MaterialFlags.GENERATE_PLASMA);
    }

    private static IParticleMaterial createAntimatterLepton(String name, int id, int color, double mass, double charge,
                                                            double spin) {
        return buildParticleMaterial(name, id, color, ParticleType.ANTI_LEPTON, mass, charge, spin, -1,
                MaterialIconSet.SHINY, new BuilderConfigurer() {
                    @Override
                    public void configure(Material.Builder builder) {
                        builder.ingot().dust();
                    }
                },
                MaterialFlags.PARTICLE,
                MaterialFlags.LEPTON,
                MaterialFlags.ANTIMATTER,
                MaterialFlags.GENERATE_INGOT,
                MaterialFlags.GENERATE_DUST);
    }

    private static IParticleMaterial buildParticleMaterial(String name, int id, int color, ParticleType type,
                                                           double mass, double charge, double spin, int halfLife,
                                                           MaterialIconSet iconSet, BuilderConfigurer configurer,
                                                           gregtech.api.unification.material.info.MaterialFlag... flags) {
        Material.Builder builder = new Material.Builder(id, new ResourceLocation(GTInsanityCore.MODID, name))
                .color(color)
                .iconSet(iconSet)
                .flags(flags);
        configurer.configure(builder);

        Material material = builder.build();
        material.setFormula(name, false);

        ParticleMaterial wrapper = (ParticleMaterial) InsanityMaterialRegistry.wrapGTCEuMaterial(material);
        wrapper.setParticleType(type);
        wrapper.setMass(mass);
        wrapper.setCharge(charge);
        wrapper.setSpin(spin);
        if (halfLife > 0) {
            wrapper.setHalfLife(halfLife);
        }
        return wrapper;
    }

    private interface BuilderConfigurer {
        void configure(Material.Builder builder);
    }
}
