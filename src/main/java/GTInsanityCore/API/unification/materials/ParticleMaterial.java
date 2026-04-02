package GTInsanityCore.API.unification.materials;

import GTInsanityCore.API.unification.flags.MaterialFlags;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialFlag;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper/Extension for GTCEu materials with GTInsanityCore-specific properties.
 * Either wraps an existing GTCEu material or creates a standalone implementation.
 */
public class ParticleMaterial implements IParticleMaterial {

    private final Material baseMaterial;
    private final String insanityName;
    private final int insanityId;
    private final Set<MaterialFlag> insanityFlags = new HashSet<MaterialFlag>();

    private double mass = 0.0;
    private double charge = 0.0;
    private double spin = 0.0;
    private int halfLife = -1;
    private boolean isAntimatter = false;
    private ParticleType particleType = ParticleType.NONE;

    public enum ParticleType {
        NONE, QUARK, LEPTON, GAUGE_BOSON, SCALAR_BOSON, ANTI_QUARK, ANTI_LEPTON
    }

    public ParticleMaterial(Material baseMaterial) {
        this.baseMaterial = baseMaterial;
        this.insanityName = baseMaterial.getName();
        this.insanityId = baseMaterial.getId();
    }

    public ParticleMaterial(String name, int id) {
        this.baseMaterial = null;
        this.insanityName = name;
        this.insanityId = id;
    }

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
        return insanityName;
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
        if (insanityFlags.contains(flag)) {
            return true;
        }
        return baseMaterial != null && baseMaterial.hasFlag(flag);
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
        this.isAntimatter = type == ParticleType.ANTI_QUARK || type == ParticleType.ANTI_LEPTON;
    }

    @Override
    public boolean hasPlasma() {
        return hasFlag(MaterialFlags.GENERATE_PLASMA);
    }

    @Override
    public boolean hasGas() {
        return hasFlag(MaterialFlags.GENERATE_GAS);
    }

    @Override
    public boolean hasIngot() {
        return hasFlag(MaterialFlags.GENERATE_INGOT);
    }

    @Override
    public boolean hasDust() {
        return hasFlag(MaterialFlags.GENERATE_DUST);
    }

    @Override
    public boolean requiresContainment() {
        return hasFlag(MaterialFlags.COLOR_CONFINEMENT) ||
                hasFlag(MaterialFlags.REQUIRES_CONTAINMENT);
    }

    @Override
    public boolean isUnstable() {
        return hasFlag(MaterialFlags.INSTABLE_HALFLIFE) || halfLife > 0;
    }

    @Override
    public int getHalfLife() {
        return halfLife;
    }

    @Override
    public void setHalfLife(int ticks) {
        this.halfLife = ticks;
        if (ticks > 0) {
            addFlag(MaterialFlags.INSTABLE_HALFLIFE);
        }
    }

    @Override
    public boolean isColorConfined() {
        return isQuark() || hasFlag(MaterialFlags.COLOR_CONFINEMENT);
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
