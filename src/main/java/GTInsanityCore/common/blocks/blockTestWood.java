package GTInsanityCore.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class blockTestWood extends Block{
    public blockTestWood(){
        super(Material.WOOD);
        this.setHardness(1.0f);
        this.setResistance(10.0f);
        this.setLightLevel(1.0f);
        this.setHarvestLevel("axe", 1);
        this.setSoundType(SoundType.WOOD);


    }
}
