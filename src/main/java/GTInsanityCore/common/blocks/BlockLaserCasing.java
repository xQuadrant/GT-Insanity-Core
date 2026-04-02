package GTInsanityCore.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockLaserCasing extends Block {

    public BlockLaserCasing() {
        super(Material.IRON);
        setHardness(4.0f);
        setResistance(15.0f);
        setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.METAL);
    }
}
