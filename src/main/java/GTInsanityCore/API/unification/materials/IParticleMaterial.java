package GTInsanityCore.API.unification.materials;

import GTInsanityCore.API.unification.flags.InsanityMaterialFlags;
import GTInsanityCore.API.unification.flags.MaterialFlags;

/**
 * Extended material interface for GTInsanityCore particle physics and exotic materials.
 * Wraps or extends GTCEu materials with additional functionality.
 */
public interface IInsanityMaterial {

    // ===== Identity =====

    /** Get the material's registry name (e.g., "up_quark") */
    String getName();

    /** Get the material's display name for tooltips */
    String getLocalizedName();

    /** Get the material ID */
    int getId();

    // ===== Chemical/Physical Properties =====

    /** Get chemical formula (e.g., "u", "e⁻", "H⁰") */
    String getChemicalFormula();

    /** Get mass in MeV/c² (for particles) or g/mol (for elements) */
    double getMass();

    /** Get electric charge in elementary charge units */
    double getCharge();

    /** Get spin in ℏ units */
    double getSpin();

    // ===== GTInsanityCore Flags =====

    /** Check if material has a specific flag */
    boolean hasFlag(InsanityMaterialFlags flag);

    /** Add a flag to this material */
    void addFlag(InsanityMaterialFlags flag);

    /** Remove a flag from this material */
    void removeFlag(InsanityMaterialFlags flag);

    // ===== Material Type Classification =====

    boolean isParticle();        // Elementary particle
    boolean isQuark();         // Quark (confined)
    boolean isLepton();        // Lepton (electron, muon, tau, neutrinos)
    boolean isGaugeBoson();    // Force carriers (photon, gluon, W, Z)
    boolean isScalarBoson();   // Higgs
    boolean isAntimatter();    // Antiparticle variant

    // ===== State Properties =====

    boolean hasPlasma();       // Has plasma state (quarks, gluons, bosons)
    boolean hasGas();          // Has gas state (neutrinos, photons)
    boolean hasIngot();        // Can be solidified (leptons)
    boolean hasDust();         // Has dust form

    // ===== Containment & Stability =====

    /** Check if particle requires quantum containment (color confinement) */
    boolean requiresContainment();

    /** Check if particle decays over time */
    boolean isUnstable();

    /** Get half-life in ticks (20 ticks = 1 second) */
    int getHalfLife();

    /** Check if particle is currently color-confined */
    boolean isColorConfined();

    // ===== GTCEu Integration =====

    /** Get underlying GTCEu material (if wrapped) */
    Object getBaseMaterial();  // Returns gregtech.api.unification.material.Material

    /** Get material color for rendering */
    int getMaterialColor();

    /** Get material icon set */
    String getIconSetName();

    // ===== NBT Serialization =====

    /** Write material to NBT */
    net.minecraft.nbt.NBTTagCompound serializeNBT();

    /** Read material from NBT */
    static IInsanityMaterial deserializeNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        return GTInsanityCore.API.unification.materials.InsanityMaterialRegistry.getMaterial(nbt.getString("MaterialName"));
    }
}
