package GTInsanityCore;

import GTInsanityCore.API.unification.GTIMaterials;
import GTInsanityCore.common.GTIEventHandler;
import GTInsanityCore.API.interaction.IFallingBlockInteraction;
import GTInsanityCore.client.GTICreativeTabs;
import GTInsanityCore.common.interaction.FallingBlockInteractionManager;
import GTInsanityCore.common.items.GTIItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "gtinsanitycore", name = "GT:Insanity Core", version = "1.0", acceptedMinecraftVersions = "[1.12.2]", dependencies = "required-after:gregtech@[2.8.10-beta,")
public class GTInsanityCore
{
    public static final String MODID = "gtinsanitycore";
    public static final String NAME = "GT:Insanity Core";
    public static final String VERSION = "1.0";

    @Mod.Instance(GTInsanityCore.MODID)
    public static GTInsanityCore instance;
    public static Logger logger;
    public static CreativeTabs TAB_INSANITY;

    private static FallingBlockInteractionManager interactionManager;
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        GTIMaterials.init();
        logger.info("GTInsanityCore materials registered.");
        GTICreativeTabs.init();
        TAB_INSANITY = GTICreativeTabs.TAB_INSANITY;
        logger.info("GTInsanityCore creative tabs registered.");
        GTIItems.init();
        logger.info("GTInsanityCore items registered.");
        logger.info("GTInsanityCore preInit complete.");
        interactionManager = FallingBlockInteractionManager.getInstance();

    }

    @EventHandler
    public void init(FMLInitializationEvent event){
        logger.info("GTInsanity Initialized");
    }

    public static void registerInteraction(IFallingBlockInteraction interaction) {
        if (interactionManager != null) {
            interactionManager.registerInteraction(interaction);
        }
    }

    public static FallingBlockInteractionManager getInteractionManager() {
        return interactionManager;
    }
}
