package GTInsanityCore.common.interaction.impl;

import GTInsanityCore.API.interaction.IFallingBlockInteraction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StoneToCobblestoneInteraction implements IFallingBlockInteraction {

    @Override
    public boolean shouldApply(EntityFallingBlock fallingBlock, Block blockBelow) {
        return blockBelow instanceof BlockStone;
    }

    @Override
    public void onAnvilLand(World world, BlockPos pos, EntityFallingBlock fallingBlock, Block blockBelow) {
        world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());

        world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_STONE_BREAK,
                SoundCategory.BLOCKS,
                1.0f,
                0.8f
        );

        for (int i = 0; i < 15; i++) {
            world.spawnParticle(
                    EnumParticleTypes.BLOCK_CRACK,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    (world.rand.nextDouble() - 0.5) * 0.5,
                    world.rand.nextDouble() * 0.5,
                    (world.rand.nextDouble() - 0.5) * 0.5,
                    Block.getStateId(Blocks.STONE.getDefaultState())
            );
        }
    }

    @Override
    public String getInteractionName() {
        return "Stone to Cobblestone";
    }
}