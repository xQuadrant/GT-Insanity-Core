package GTInsanityCore.API.unification.materials;

import gregtech.api.unification.material.info.MaterialFlag;

/**
 * Extended material interface for GTInsanityCore particle physics and exotic materials.
 * Wraps or extends GTCEu materials with additional functionality.
 */
public interface IParticleMaterial {

    // ===== Identity =====

    /** Get the material's registry name (e.g., "up_quark") */
    String getName();

    /** Get the material's display name for tooltips */
    String getLocalizedName();

    /** Get the material ID */
    int getId();

    // ===== Chemical/Physical Properties =====

    /** Get chemical formula (e.g., "u", "electron", "hydrogen") */
    String getChemicalFormula();

    /** Get mass in MeV/c^2 (for particles) or g/mol (for elements) */
    double getMass();

    /** Get electric charge in elementary charge units */
    double getCharge();

    /** Get spin in h-bar units */
    double getSpin();

    // ===== GTInsanityCore Flags =====

    /** Check if material has a specific flag */
    boolean hasFlag(MaterialFlag flag);

    /** Add a flag to this material */
    void addFlag(MaterialFlag flag);

    /** Remove a flag from this material */
    void removeFlag(MaterialFlag flag);

    // ===== Material Type Classification =====

    boolean isParticle();
    boolean isQuark();
    boolean isLepton();
    boolean isGaugeBoson();
    boolean isScalarBoson();
    boolean isAntimatter();

    // ===== State Properties =====

    boolean hasPlasma();
    boolean hasGas();
    boolean hasIngot();
    boolean hasDust();

    // ===== Containment & Stability =====

    boolean requiresContainment();

    boolean isUnstable();

    /** Get half-life in ticks (20 ticks = 1 second) */
    int getHalfLife();

    boolean isColorConfined();

    // ===== GTCEu Integration =====

    /** Get underlying GTCEu material (if wrapped) */
    Object getBaseMaterial();

    /** Get material color for rendering */
    int getMaterialColor();

    /** Get material icon set */
    String getIconSetName();

    // ===== NBT Serialization =====

    /** Write material to NBT */
    net.minecraft.nbt.NBTTagCompound serializeNBT();

    /** Read material from NBT */
    static IParticleMaterial deserializeNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        return GTInsanityCore.API.unification.materials.InsanityMaterialRegistry.getMaterial(nbt.getString("MaterialName"));
    }

    void setHalfLife(int halfLife);
}
