package GTInsanityCore.common.interaction;

import java.util.*;

import GTInsanityCore.API.managers.IFallingBlockInteractionManager;
import com.google.common.collect.Lists;

import GTInsanityCore.API.interaction.IFallingBlockInteraction;
import GTInsanityCore.common.interaction.impl.*;
import net.minecraft.block.BlockAnvil;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FallingBlockInteractionManager implements IFallingBlockInteractionManager {

    private static FallingBlockInteractionManager instance;
    private final List<IFallingBlockInteraction> interactions = Lists.newArrayList();
    private final Map<UUID, FallingBlockData> pendingFallingBlocks = new HashMap<>();

    private FallingBlockInteractionManager() {
        registerDefaultInteractions();
    }

    public static FallingBlockInteractionManager getInstance() {
        if (instance == null) {
            instance = new FallingBlockInteractionManager();
        }
        return instance;
    }

    private void registerDefaultInteractions() {
        registerInteraction(new StoneToCobblestoneInteraction());
    }

    public void registerInteraction(IFallingBlockInteraction interaction) {
        if (!interactions.contains(interaction)) {
            interactions.add(interaction);
            interactions.sort(Comparator.comparingInt(IFallingBlockInteraction::getPriority).reversed());
        }
    }

    public void unregisterInteraction(IFallingBlockInteraction interaction) {
        interactions.remove(interaction);
    }

    public int getInteractionCount() {
        return interactions.size();
    }


    public void addPendingFallingBlock(EntityFallingBlock fallingBlock) {
        if (fallingBlock.getBlock() instanceof BlockAnvil) {
            pendingFallingBlocks.put(
                    fallingBlock.getUniqueID(),
                    new FallingBlockData(fallingBlock, 200)
            );
        }
    }

    public void tick() {
        Iterator<Map.Entry<UUID, FallingBlockData>> iterator =
                pendingFallingBlocks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, FallingBlockData> entry = iterator.next();
            FallingBlockData data = entry.getValue();

            if (data.timer > 0) {
                data.timer--;
                continue;
            }

            processFallingBlockLanding(data);
            iterator.remove();
        }
    }

    private void processFallingBlockLanding(FallingBlockData data) {
        if (data.fallingBlock == null || data.fallingBlock.isDead) return;

        World world = data.fallingBlock.world;
        BlockPos pos = data.fallingBlock.getPosition();
        BlockPos belowPos = pos.down();

        if (belowPos.getY() < 0) return;

        net.minecraft.block.Block blockBelow = world.getBlockState(belowPos).getBlock();

        for (IFallingBlockInteraction interaction : interactions) {
            if (interaction.shouldApply(data.fallingBlock, blockBelow)) {
                interaction.onAnvilLand(world, belowPos, data.fallingBlock, blockBelow);
                break;
            }
        }
    }

    private static class FallingBlockData {
        EntityFallingBlock fallingBlock;
        int timer;

        FallingBlockData(EntityFallingBlock fallingBlock, int timer) {
            this.fallingBlock = fallingBlock;
            this.timer = timer;
        }
    }
}