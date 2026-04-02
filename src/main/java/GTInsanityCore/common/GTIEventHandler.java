package GTInsanityCore.common;

import GTInsanityCore.GTInsanityCore;
import GTInsanityCore.common.blocks.GTIBlocks;
import GTInsanityCore.common.interaction.FallingBlockInteractionManager;
import GTInsanityCore.common.items.GTIItems;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


@Mod.EventBusSubscriber(modid = GTInsanityCore.MODID)
public class GTIEventHandler {
    private static final FallingBlockInteractionManager interactionManager = FallingBlockInteractionManager.getInstance();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerBlock(RegistryEvent.Register<Block> event){
        GTIBlocks.init();
        GTIBlocks.registerBlocks(event.getRegistry());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerItem(RegistryEvent.Register<Item> event){
        GTIItems.init(); // Initialize items
        GTIItems.registerItemBlocks(event.getRegistry());
        GTIBlocks.registerItemBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityFallingBlock) {
            EntityFallingBlock fallingBlock = (EntityFallingBlock) event.getEntity();

            if (interactionManager != null) {
                interactionManager.addPendingFallingBlock(fallingBlock);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.world.isRemote) {
            if (interactionManager != null) {
                interactionManager.tick();
            }
        }
    }
}
