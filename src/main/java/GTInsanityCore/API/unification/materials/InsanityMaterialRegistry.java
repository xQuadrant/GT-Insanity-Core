package GTInsanityCore.API.unification.materials;

import GTInsanityCore.GTInsanityCore;
import gregtech.api.unification.material.registry.MaterialRegistry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Central registry for all GTInsanityCore materials.
 * Bridges GTCEu materials and standalone InsanityCore materials.
 */
public class InsanityMaterialRegistry {

    private static final Map<String, IParticleMaterial> MATERIALS = new HashMap<>();
    private static final Map<Integer, IParticleMaterial> ID_TO_MATERIAL = new HashMap<>();

    // ID ranges
    public static final int INSANITY_ID_START = 32100;
    public static final int INSANITY_ID_END = 32700;
    private static int nextId = INSANITY_ID_START;

    /**
     * Register a new InsanityCore material
     */
    public static IParticleMaterial registerMaterial(ParticleMaterial material) {
        String name = material.getName();
        int id = material.getId();

        if (MATERIALS.containsKey(name)) {
            GTInsanityCore.logger.error("Duplicate material registration: " + name);
            return MATERIALS.get(name);
        }

        if (ID_TO_MATERIAL.containsKey(id)) {
            GTInsanityCore.logger.error("Duplicate material ID: " + id + " for " + name);
            return ID_TO_MATERIAL.get(id);
        }

        MATERIALS.put(name, material);
        ID_TO_MATERIAL.put(id, material);

        GTInsanityCore.logger.info("Registered InsanityMaterial: " + name + " (ID: " + id + ")");
        return material;
    }

    /**
     * Wrap existing GTCEu material with extended properties
     */
    public static IParticleMaterial wrapGTCEuMaterial(gregtech.api.unification.material.Material gtMaterial) {
        String name = gtMaterial.getName();

        if (MATERIALS.containsKey(name)) {
            return MATERIALS.get(name);
        }

        ParticleMaterial wrapper = new ParticleMaterial(gtMaterial);
        return registerMaterial(wrapper);
    }

    /**
     * Create and register a new standalone material
     */
    public static IParticleMaterial createMaterial(String name) {
        if (MATERIALS.containsKey(name)) {
            return MATERIALS.get(name);
        }

        int id = nextId++;
        if (id > INSANITY_ID_END) {
            throw new IllegalStateException("Ran out of InsanityMaterial IDs!");
        }

        ParticleMaterial material = new ParticleMaterial(name, id);
        return registerMaterial(material);
    }

    /**
     * Get material by name
     */
    @Nullable
    public static IParticleMaterial getMaterial(String name) {
        return MATERIALS.get(name);
    }

    /**
     * Get material by ID
     */
    @Nullable
    public static IParticleMaterial getMaterialById(int id) {
        return ID_TO_MATERIAL.get(id);
    }

    /**
     * Check if material exists
     */
    public static boolean hasMaterial(String name) {
        return MATERIALS.containsKey(name);
    }

    /**
     * Get all registered materials
     */
    public static Collection<IParticleMaterial> getAllMaterials() {
        return MATERIALS.values();
    }

    /**
     * Get all particle materials
     */
    public static Collection<IParticleMaterial> getParticles() {
        return MATERIALS.values().stream()
                .filter(IParticleMaterial::isParticle)
                .toList();
    }

    /**
     * Get all quarks
     */
    public static Collection<IParticleMaterial> getQuarks() {
        return MATERIALS.values().stream()
                .filter(IParticleMaterial::isQuark)
                .toList();
    }

    /**
     * Get all leptons
     */
    public static Collection<IParticleMaterial> getLeptons() {
        return MATERIALS.values().stream()
                .filter(IParticleMaterial::isLepton)
                .toList();
    }

    /**
     * Validate no ID conflicts with GTCEu
     */
    public static void validateIDs() {
        for (int id = INSANITY_ID_START; id < nextId; id++) {
            gregtech.api.unification.material.Material gtMat =
                    gregtech.api.unification.material.MaterialRegistry.getMaterialById(id);
            if (gtMat != null) {
                GTInsanityCore.logger.error("ID CONFLICT: GTCEu material occupies ID " + id +
                        " (" + gtMat.getName() + ")");
            }
        }
    }

    /**
     * Clear registry (for testing only!)
     */
    public static void clear() {
        MATERIALS.clear();
        ID_TO_MATERIAL.clear();
        nextId = INSANITY_ID_START;
    }
}