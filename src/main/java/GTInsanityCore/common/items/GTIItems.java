package GTInsanityCore.common.items;

import GTInsanityCore.GTInsanityCore;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class GTIItems {

    // Particle containment items
    public static ItemPlasmaContainer PLASMA_CONTAINER;
    //public static ItemStrata STRATA_ITEM;
    //public static ItemResearchToken RESEARCH_TOKEN;

    public static void init() {
        if (PLASMA_CONTAINER == null) {
            PLASMA_CONTAINER = createItem("plasma_container", new ItemPlasmaContainer());
        }
        //STRATA_ITEM = createItem("strata", new ItemStrata());
        //RESEARCH_TOKEN = createItem("research_token", new ItemResearchToken());
    }

    private static <T extends Item> T createItem(String name, T item) {
        item.setRegistryName(GTInsanityCore.MODID, name);
        item.setUnlocalizedName(GTInsanityCore.MODID + "." + name);
        item.setCreativeTab(GTInsanityCore.TAB_INSANITY); // Create this creative tab!
        return item;
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        init();
        registry.register(PLASMA_CONTAINER);
    }
}
