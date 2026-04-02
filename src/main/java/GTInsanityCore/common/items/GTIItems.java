package GTInsanityCore.common.items;

import GTInsanityCore.GTInsanityCore;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class GTIItems {

    // Particle containment items
    public static ItemPlasmaContainer PLASMA_CONTAINER;
    //public static ItemStrata STRATA_ITEM;
    //public static ItemResearchToken RESEARCH_TOKEN;

    public static void init() {
        PLASMA_CONTAINER = registerItem("plasma_container", new ItemPlasmaContainer());
        //STRATA_ITEM = registerItem("strata", new ItemStrata());
        //RESEARCH_TOKEN = registerItem("research_token", new ItemResearchToken());
    }

    private static <T extends Item> T registerItem(String name, T item) {
        item.setRegistryName(GTInsanityCore.MODID, name);
        item.setUnlocalizedName(GTInsanityCore.MODID + "." + name);
        item.setCreativeTab(GTInsanityCore.TAB_INSANITY); // Create this creative tab!
        GameRegistry.findRegistry(Item.class).register(item);
        return item;
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        // If you have item-blocks to register
    }
}
