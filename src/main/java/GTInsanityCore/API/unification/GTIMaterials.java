package GTInsanityCore.API.unification;

import GTInsanityCore.API.unification.materials.IParticleMaterial;
import GTInsanityCore.API.unification.materials.ParticleMaterial;
import GTInsanityCore.API.unification.materials.InsanityMaterialRegistry;
import GTInsanityCore.API.unification.materials.ParticleMaterial.ParticleType;
import GTInsanityCore.API.unification.flags.MaterialFlags;

public class GTIMaterials {

    // Now these are IInsanityMaterial, not raw GTCEu Material
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

    // Antimatter counterparts
    public static IParticleMaterial Positron;
    public static IParticleMaterial AntiUpQuark;
    public static IParticleMaterial AntiDownQuark;

    public static void init() {
        // === QUARKS ===
        UpQuark = createQuark("up_quark", 2.2, 0.667, 0.5);
        DownQuark = createQuark("down_quark", 4.7, -0.333, 0.5);
        CharmQuark = createQuark("charm_quark", 1275, 0.667, 0.5);
        StrangeQuark = createQuark("strange_quark", 95, -0.333, 0.5);
        TopQuark = createHeavyQuark("top_quark", 173000, 0.667, 0.5, 20 * 5); // 5s half-life
        BottomQuark = createQuark("bottom_quark", 4180, -0.333, 0.5);

        // === LEPTONS ===
        Electron = createLepton("electron", 0.511, -1.0, 0.5, true); // Can be contained
        Muon = createUnstableLepton("muon", 105.7, -1.0, 0.5, 20 * 120); // 2min half-life
        Tau = createUnstableLepton("tau", 1777, -1.0, 0.5, 20 * 300); // 5min half-life

        ElectronNeutrino = createNeutrino("electron_neutrino", 0.0000022, 0, 0.5);
        MuonNeutrino = createNeutrino("muon_neutrino", 0.17, 0, 0.5);
        TauNeutrino = createNeutrino("tau_neutrino", 15.5, 0, 0.5);

        // === BOSONS ===
        Photon = createGaugeBoson("photon", 0, 0, 1.0);
        Gluon = createGluon("gluon", 0, 0, 1.0);
        WBosonPlus = createUnstableBoson("w_boson_plus", 80400, 1.0, 1.0, 20 * 10);
        WBosonMinus = createUnstableBoson("w_boson_minus", 80400, -1.0, 1.0, 20 * 10);
        ZBoson = createUnstableBoson("z_boson", 91200, 0, 1.0, 20 * 15);

        HiggsBoson = createScalarBoson("higgs_boson", 125100, 0, 0);

        // === ANTIMATTER ===
        Positron = createAntimatterLepton("positron", 0.511, 1.0, 0.5);
        AntiUpQuark = createAntimatterQuark("anti_up_quark", 2.2, -0.667, 0.5);
        AntiDownQuark = createAntimatterQuark("anti_down_quark", 4.7, 0.333, 0.5);

        // Validate no ID conflicts
        InsanityMaterialRegistry.validateIDs();
    }

    // ===== Factory Methods =====

    private static IParticleMaterial createQuark(String name, double mass, double charge, double spin) {
        ParticleMaterial mat = (ParticleMaterial) InsanityMaterialRegistry.createMaterial(name);
        mat.setParticleType(ParticleType.QUARK);
        mat.setMass(mass);
        mat.setCharge(charge);
        mat.setSpin(spin);
        mat.addFlag(MaterialFlags.PARTICLE);
        mat.addFlag(MaterialFlags.QUARK);
        mat.addFlag(MaterialFlags.COLOR_CONFINEMENT);
        mat.addFlag(MaterialFlags.REQUIRES_CONTAINMENT);
        mat.addFlag(MaterialFlags.GENERATE_PLASMA);
        mat.addFlag(MaterialFlags.DISABLE_DECOMPOSITION);
        return mat;
    }

    private static IParticleMaterial createHeavyQuark(String name, double mass, double charge, double spin, int halfLife) {
        IParticleMaterial mat = createQuark(name, mass, charge, spin);
        mat.setHalfLife(halfLife);
        return mat;
    }

    private static IParticleMaterial createLepton(String name, double mass, double charge, double spin, boolean canContain) {
        ParticleMaterial mat = (ParticleMaterial) InsanityMaterialRegistry.createMaterial(name);
        mat.setParticleType(ParticleType.LEPTON);
        mat.setMass(mass);
        mat.setCharge(charge);
        mat.setSpin(spin);
        mat.addFlag(MaterialFlags.PARTICLE);
        mat.addFlag(MaterialFlags.LEPTON);
        if (canContain) {
            mat.addFlag(MaterialFlags.GENERATE_INGOT);
            mat.addFlag(MaterialFlags.GENERATE_DUST);
        }
        return mat;
    }

    private static IParticleMaterial createUnstableLepton(String name, double mass, double charge, double spin, int halfLife) {
        IParticleMaterial mat = createLepton(name, mass, charge, spin, true);
        mat.setHalfLife(halfLife);
        mat.addFlag(MaterialFlags.WEAK_DECAY);
        return mat;
    }

    private static IParticleMaterial createNeutrino(String name, double mass, double charge, double spin) {
        ParticleMaterial mat = (ParticleMaterial) InsanityMaterialRegistry.createMaterial(name);
        mat.setParticleType(ParticleType.LEPTON);
        mat.setMass(mass);
        mat.setCharge(charge);
        mat.setSpin(spin);
        mat.addFlag(MaterialFlags.PARTICLE);
        mat.addFlag(MaterialFlags.LEPTON);
        mat.addFlag(MaterialFlags.NEUTRINO);
        mat.addFlag(MaterialFlags.GENERATE_GAS);
        mat.addFlag(MaterialFlags.DISABLE_DECOMPOSITION);
        return mat;
    }

    private static IParticleMaterial createGaugeBoson(String name, double mass, double charge, double spin) {
        ParticleMaterial mat = (ParticleMaterial) InsanityMaterialRegistry.createMaterial(name);
        mat.setParticleType(ParticleType.GAUGE_BOSON);
        mat.setMass(mass);
        mat.setCharge(charge);
        mat.setSpin(spin);
        mat.addFlag(MaterialFlags.PARTICLE);
        mat.addFlag(MaterialFlags.GAUGE_BOSON);
        mat.addFlag(MaterialFlags.GENERATE_GAS);
        return mat;
    }

    private static IParticleMaterial createGluon(String name, double mass, double charge, double spin) {
        ParticleMaterial mat = (ParticleMaterial) InsanityMaterialRegistry.createMaterial(name);
        mat.setParticleType(ParticleType.GAUGE_BOSON);
        mat.setMass(mass);
        mat.setCharge(charge);
        mat.setSpin(spin);
        mat.addFlag(MaterialFlags.PARTICLE);
        mat.addFlag(MaterialFlags.GAUGE_BOSON);
        mat.addFlag(MaterialFlags.GENERATE_PLASMA);
        mat.addFlag(MaterialFlags.COLOR_CONFINEMENT);
        mat.addFlag(MaterialFlags.DISABLE_DECOMPOSITION);
        return mat;
    }

    private static IParticleMaterial createUnstableBoson(String name, double mass, double charge, double spin, int halfLife) {
        IParticleMaterial mat = createGaugeBoson(name, mass, charge, spin);
        mat.setHalfLife(halfLife);
        mat.removeFlag(MaterialFlags.GENERATE_GAS);
        mat.addFlag(MaterialFlags.GENERATE_PLASMA);
        return mat;
    }

    private static IParticleMaterial createScalarBoson(String name, double mass, double charge, double spin) {
        ParticleMaterial mat = (ParticleMaterial) InsanityMaterialRegistry.createMaterial(name);
        mat.setParticleType(ParticleType.SCALAR_BOSON);
        mat.setMass(mass);
        mat.setCharge(charge);
        mat.setSpin(spin);
        mat.setHalfLife(20 * 20); // 20 seconds
        mat.addFlag(MaterialFlags.PARTICLE);
        mat.addFlag(MaterialFlags.SCALAR_BOSON);
        mat.addFlag(MaterialFlags.GENERATE_PLASMA);
        mat.addFlag(MaterialFlags.INSTABLE_HALFLIFE);
        mat.addFlag(MaterialFlags.DISABLE_DECOMPOSITION);
        return mat;
    }

    private static IParticleMaterial createAntimatterQuark(String name, double mass, double charge, double spin) {
        ParticleMaterial mat = (ParticleMaterial) InsanityMaterialRegistry.createMaterial(name);
        mat.setParticleType(ParticleType.ANTI_QUARK);
        mat.setMass(mass);
        mat.setCharge(charge);
        mat.setSpin(spin);
        mat.addFlag(MaterialFlags.PARTICLE);
        mat.addFlag(MaterialFlags.QUARK);
        mat.addFlag(MaterialFlags.ANTIMATTER);
        mat.addFlag(MaterialFlags.COLOR_CONFINEMENT);
        mat.addFlag(MaterialFlags.REQUIRES_CONTAINMENT);
        mat.addFlag(MaterialFlags.GENERATE_PLASMA);
        return mat;
    }

    private static IParticleMaterial createAntimatterLepton(String name, double mass, double charge, double spin) {
        ParticleMaterial mat = (ParticleMaterial) InsanityMaterialRegistry.createMaterial(name);
        mat.setParticleType(ParticleType.ANTI_LEPTON);
        mat.setMass(mass);
        mat.setCharge(charge);
        mat.setSpin(spin);
        mat.addFlag(MaterialFlags.PARTICLE);
        mat.addFlag(MaterialFlags.LEPTON);
        mat.addFlag(MaterialFlags.ANTIMATTER);
        mat.addFlag(MaterialFlags.GENERATE_INGOT);
        mat.addFlag(MaterialFlags.GENERATE_DUST);
        return mat;
    }
}
