package GTInsanityCore;

import GTInsanityCore.common.GTIEventHandler;
import GTInsanityCore.API.interaction.IFallingBlockInteraction;
import GTInsanityCore.common.interaction.FallingBlockInteractionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "gtinsanitycore", name = "GT:Insanity Core", version = "1.0", acceptedMinecraftVersions = "[1.12.2]", dependencies = "required-after:gregtech")
public class GTInsanityCore
{
    public static final String MODID = "gtinsanitycore";
    public static final String NAME = "GT:Insanity Core";
    public static final String VERSION = "1.0";

    @Mod.Instance(GTInsanityCore.MODID)
    public static GTInsanityCore instance;
    private static Logger logger;

    private static FallingBlockInteractionManager interactionManager;
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
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
