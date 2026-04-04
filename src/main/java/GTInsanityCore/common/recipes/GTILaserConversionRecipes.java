package GTInsanityCore.common.recipes;

import GTInsanityCore.GTInsanityCore;
import gregtech.api.GTValues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Registro de configuracoes de conversao de laser por tier.
 * Define EU/t e eficiencia para cada tier de energia.
 */
public final class GTILaserConversionRecipes {

    private static final Logger LOGGER = LogManager.getLogger(GTInsanityCore.MODID + "/LaserRecipes");

    private static boolean initialized = false;

    /**
     * Dados de conversao para um tier especifico.
     */
    public static final class LaserConversionData {
        public final int tier;
        public final long euPerTick;
        public final int efficiencyPercent;
        public final long luPerCycle;

        public LaserConversionData(int tier, long euPerTick, int efficiencyPercent, long luPerCycle) {
            this.tier = tier;
            this.euPerTick = euPerTick;
            this.efficiencyPercent = efficiencyPercent;
            this.luPerCycle = luPerCycle;
        }
    }

    // Array de dados de conversao por tier (LV=1..OpV=14)
    public static final LaserConversionData[] CONVERSION_DATA = new LaserConversionData[GTValues.V.length];

    private GTILaserConversionRecipes() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        // Registrar dados de conversao para cada tier
        for (int tier = GTValues.LV; tier <= GTValues.OpV; tier++) {
            long euPerTick = GTValues.V[tier];
            int efficiency = Math.max(1, 100 - ((tier - GTValues.LV) * 2));
            long euPerCycle = euPerTick * 20; // 20 ticks = 1 segundo
            long luPerCycle = (euPerCycle * efficiency) / 100L;

            CONVERSION_DATA[tier] = new LaserConversionData(tier, euPerTick, efficiency, luPerCycle);

            LOGGER.debug("Laser conversion for tier {}: {} EU/t -> {} LU/cycle ({}% efficiency)",
                    GTValues.VN[tier], euPerTick, luPerCycle, efficiency);
        }

        LOGGER.info("Registered laser conversion data for all tiers");
    }

    /**
     * Obtem dados de conversao para um tier especifico.
     */
    public static LaserConversionData getConversionData(int tier) {
        if (tier < 0 || tier >= CONVERSION_DATA.length || CONVERSION_DATA[tier] == null) {
            return null;
        }
        return CONVERSION_DATA[tier];
    }
}
