package GTInsanityCore.common.laser;

import gregtech.api.GTValues;

public final class LaserUnitTiers {

    private static final long DEFAULT_BUFFER_MULTIPLIER = 256L;

    private LaserUnitTiers() {
    }

    public static long transferRate(int tier) {
        if (tier < 0 || tier >= GTValues.V.length) {
            return 0L;
        }
        return GTValues.V[tier];
    }

    public static long bufferForTier(int tier) {
        return saturatingMultiply(transferRate(tier), DEFAULT_BUFFER_MULTIPLIER);
    }

    public static long saturatingMultiply(long left, long right) {
        if (left <= 0L || right <= 0L) {
            return 0L;
        }
        if (left > Long.MAX_VALUE / right) {
            return Long.MAX_VALUE;
        }
        return left * right;
    }
}
