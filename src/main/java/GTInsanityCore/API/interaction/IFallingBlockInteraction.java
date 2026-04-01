package GTInsanityCore.API.interaction;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFallingBlockInteraction {
    boolean shouldApply(EntityFallingBlock fallingBlock, Block blockBelow);

    void onAnvilLand(World world, BlockPos pos, EntityFallingBlock fallingBlock, Block blockBelow);

    default int getPriority() {
        return 0;
    }

    default String getInteractionName() {
        return this.getClass().getSimpleName();
    }
}
