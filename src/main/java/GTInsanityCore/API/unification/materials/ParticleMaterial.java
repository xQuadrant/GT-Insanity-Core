package GTInsanityCore.API.unification.materials;

import GTInsanityCore.API.unification.flags.InsanityMaterialFlags;
import GTInsanityCore.API.unification.materials.MaterialFlag;
import GTInsanityCore.API.unification.materials.IInsanityMaterial;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.MaterialRegistry;
import gregtech.api.unification.material.info.MaterialFlags;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper/Extension for GTCEu materials with GTInsanityCore-specific properties.
 * Either wraps an existing GTCEu material or creates a standalone implementation.
 */
public class InsanityMaterial implements IInsanityMaterial {

    // Underlying GTCEu material (null for pure InsanityCore materials)
    private final Material baseMaterial;

    // GTInsanityCore-specific data (extends GTCEu)
    private final String insanityName;
    private final int insanityId;
    private final Set<MaterialFlags> insanityFlags = new HashSet<>();

    // Particle physics data
    private double mass = 0.0;           // MeV/c²
    private double charge = 0.0;         // Elementary charge
    private double spin = 0.0;           // ℏ units
    private int halfLife = -1;           // Ticks, -1 = stable
    private boolean isAntimatter = false;
    private ParticleType particleType = ParticleType.NONE;

    public enum ParticleType {
        NONE, QUARK, LEPTON, GAUGE_BOSON, SCALAR_BOSON, ANTI_QUARK, ANTI_LEPTON
    }

    // ===== Constructors =====

    /**
     * Wrap existing GTCEu material with extended properties
     */
    public InsanityMaterial(Material baseMaterial) {
        this.baseMaterial = baseMaterial;
        this.insanityName = baseMaterial.getName();
        this.insanityId = baseMaterial.getId();
    }

    /**
     * Create standalone InsanityCore material (no GTCEu backing)
     */
    public InsanityMaterial(String name, int id) {
        this.baseMaterial = null;
        this.insanityName = name;
        this.insanityId = id;
    }

    // ===== IInsanityMaterial Implementation =====

    @Override
    public String getName() {
        return baseMaterial != null ? baseMaterial.getName() : insanityName;
    }

    @Override
    public String getLocalizedName() {
        return baseMaterial != null ? baseMaterial.getLocalizedName() :
                net.minecraft.client.resources.I18n.format("material." + insanityName);
    }

    @Override
    public int getId() {
        return baseMaterial != null ? baseMaterial.getId() : insanityId;
    }

    @Override
    public String getChemicalFormula() {
        if (baseMaterial != null) {
            return baseMaterial.getChemicalFormula();
        }
        return insanityName; // Fallback
    }

    @Override
    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    @Override
    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    @Override
    public double getSpin() {
        return spin;
    }

    public void setSpin(double spin) {
        this.spin = spin;
    }

    @Override
    public boolean hasFlag(MaterialFlag flag) {
        // Check GTInsanityCore flags first
        if (insanityFlags.contains(flag)) return true;

        // Check GTCEu material flags
        if (baseMaterial != null) {
            return baseMaterial.hasFlag(flag);
        }
        return false;
    }

    @Override
    public void addFlag(MaterialFlag flag) {
        insanityFlags.add(flag);
    }

    @Override
    public void removeFlag(MaterialFlag flag) {
        insanityFlags.remove(flag);
    }

    @Override
    public boolean isParticle() {
        return particleType != ParticleType.NONE;
    }

    @Override
    public boolean isQuark() {
        return particleType == ParticleType.QUARK || particleType == ParticleType.ANTI_QUARK;
    }

    @Override
    public boolean isLepton() {
        return particleType == ParticleType.LEPTON || particleType == ParticleType.ANTI_LEPTON;
    }

    @Override
    public boolean isGaugeBoson() {
        return particleType == ParticleType.GAUGE_BOSON;
    }

    @Override
    public boolean isScalarBoson() {
        return particleType == ParticleType.SCALAR_BOSON;
    }

    @Override
    public boolean isAntimatter() {
        return isAntimatter || particleType == ParticleType.ANTI_QUARK ||
                particleType == ParticleType.ANTI_LEPTON;
    }

    public void setParticleType(ParticleType type) {
        this.particleType = type;
        this.isAntimatter = (type == ParticleType.ANTI_QUARK || type == ParticleType.ANTI_LEPTON);
    }

    @Override
    public boolean hasPlasma() {
        return hasFlag(MaterialFlag.GENERATE_PLASMA);
    }

    @Override
    public boolean hasGas() {
        return hasFlag(MaterialFlag.GENERATE_GAS);
    }

    @Override
    public boolean hasIngot() {
        return hasFlag(MaterialFlag.GENERATE_INGOT);
    }

    @Override
    public boolean hasDust() {
        return hasFlag(MaterialFlag.GENERATE_DUST);
    }

    @Override
    public boolean requiresContainment() {
        return hasFlag(MaterialFlag.FLAG_COLOR_CONFINEMENT) ||
                hasFlag(MaterialFlag.FLAG_REQUIRES_CONTAINMENT);
    }

    @Override
    public boolean isUnstable() {
        return hasFlag(MaterialFlag.FLAG_INSTABLE_HALFLIFE) || halfLife > 0;
    }

    @Override
    public int getHalfLife() {
        return halfLife;
    }

    public void setHalfLife(int ticks) {
        this.halfLife = ticks;
        if (ticks > 0) {
            addFlag(MaterialFlag.FLAG_INSTABLE_HALFLIFE);
        }
    }

    @Override
    public boolean isColorConfined() {
        return isQuark() || hasFlag(MaterialFlag.FLAG_COLOR_CONFINEMENT);
    }

    @Override
    public Object getBaseMaterial() {
        return baseMaterial;
    }

    @Override
    public int getMaterialColor() {
        if (baseMaterial != null) {
            return baseMaterial.getMaterialRGB();
        }
        return 0xFFFFFF;
    }

    @Override
    public String getIconSetName() {
        if (baseMaterial != null) {
            return baseMaterial.getMaterialIconSet().getName();
        }
        return "DULL";
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("MaterialName", getName());
        nbt.setInteger("InsanityId", insanityId);
        nbt.setDouble("Mass", mass);
        nbt.setDouble("Charge", charge);
        nbt.setDouble("Spin", spin);
        nbt.setInteger("HalfLife", halfLife);
        nbt.setInteger("ParticleType", particleType.ordinal());
        nbt.setBoolean("IsAntimatter", isAntimatter);
        return nbt;
    }

    // ===== Utility Methods =====

    /**
     * Get as GTCEu Material (if wrapped)
     */
    public Material toGTCEuMaterial() {
        if (baseMaterial == null) {
            throw new IllegalStateException("Standalone InsanityMaterial cannot convert to GTCEu Material");
        }
        return baseMaterial;
    }

    @Override
    public String toString() {
        return String.format("InsanityMaterial[%s, id=%d, type=%s, charge=%.2f]",
                getName(), getId(), particleType, charge);
    }
}