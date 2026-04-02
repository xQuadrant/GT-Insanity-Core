package GTInsanityCore.common.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;

public final class GTIRecipeMaps {

    public static final RecipeMap<SimpleRecipeBuilder> CVD_RECIPES = new RecipeMap<>(
            "cvd_chamber",
            1,
            1,
            1,
            0,
            new SimpleRecipeBuilder(),
            false)
            .setProgressBar(GuiTextures.PROGRESS_BAR_CIRCUIT, ProgressWidget.MoveType.HORIZONTAL)
            .setSlotOverlay(false, true, GuiTextures.MOLECULAR_OVERLAY_1)
            .setSlotOverlay(true, false, GuiTextures.OUT_SLOT_OVERLAY);

    private GTIRecipeMaps() {
    }

    public static void init() {
        // Intentionally left blank. Accessing the class initializes the recipe map.
    }
}
