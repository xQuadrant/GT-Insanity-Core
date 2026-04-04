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

    /**
     * Recipe map para conversao de EU em Laser Units.
     * Nao requer inputs de itens ou fluidos -- apenas energia.
     * O output e produzido via capacidade customizada LASER_OUTPUT_LU.
     */
    public static final RecipeMap<SimpleRecipeBuilder> LASER_CONVERSION_RECIPES = new RecipeMap<>(
            "laser_conversion",
            0,
            0,
            0,
            0,
            new SimpleRecipeBuilder(),
            false)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressWidget.MoveType.HORIZONTAL)
            .setSlotOverlay(false, false, GuiTextures.BLANK)
            .setSlotOverlay(true, false, GuiTextures.BLANK);

    /**
     * Recipe map para o Mob Simulator.
     * Usado para simular/spawnar mobs e gerar drops.
     */
    public static final RecipeMap<SimpleRecipeBuilder> MOB_SIMULATOR_RECIPES = new RecipeMap<>(
            "mob_simulator",
            1,
            2,
            0,
            0,
            new SimpleRecipeBuilder(),
            false)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MASS_FAB, ProgressWidget.MoveType.HORIZONTAL)
            .setSlotOverlay(false, true, GuiTextures.MOLECULAR_OVERLAY_1)
            .setSlotOverlay(true, false, GuiTextures.OUT_SLOT_OVERLAY)
            .setSlotOverlay(true, true, GuiTextures.OUT_SLOT_OVERLAY);

    private GTIRecipeMaps() {
    }

    public static void init() {
        // Intentionally left blank. Accessing the class initializes the recipe map.
    }
}
