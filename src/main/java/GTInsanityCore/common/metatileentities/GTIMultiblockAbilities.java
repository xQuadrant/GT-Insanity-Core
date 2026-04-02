package GTInsanityCore.common.metatileentities;

import GTInsanityCore.API.capability.ILaserUnitContainer;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;

public final class GTIMultiblockAbilities {

    public static final MultiblockAbility<ILaserUnitContainer> LASER_OUTPUT_LU =
            new MultiblockAbility<>("laser_output_lu");

    private GTIMultiblockAbilities() {
    }
}
