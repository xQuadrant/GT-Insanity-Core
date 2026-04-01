package GTInsanityCore.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;


public class GTIBlocks {
    public static Block TEST_WOOD;


public static void init(){
    TEST_WOOD = new blockTestWood();
    setupRegistryNames();
}

private static void setupRegistryNames() {
    TEST_WOOD.setRegistryName("test_wood");
    TEST_WOOD.setUnlocalizedName("gtinsanitycore.test_wood");
}

public static void registerBlocks(IForgeRegistry<Block> registry){
    registry.register(TEST_WOOD);
}

public static void registerItemBlocks(IForgeRegistry<Item> registry) {
    registerItemBlock(registry, TEST_WOOD);

}

private static void  registerItemBlock(@Nonnull IForgeRegistry<Item> registry, Block block){
    registry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
}}