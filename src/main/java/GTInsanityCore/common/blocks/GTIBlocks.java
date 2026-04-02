package GTInsanityCore.common.blocks;

import GTInsanityCore.GTInsanityCore;
import GTInsanityCore.common.tileentities.TileEntityLaserPort;
import GTInsanityCore.common.tileentities.TileEntityPrimitiveBulkSmelter;
import GTInsanityCore.common.tileentities.TileEntitySimpleLaserController;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;


public class GTIBlocks {
    public static Block TEST_WOOD;
    public static Block LASER_CASING;
    public static Block LASER_PORT;
    public static Block SIMPLE_LASER_CONTROLLER;
    public static Block PRIMITIVE_BULK_SMELTER;


public static void init(){
    if (TEST_WOOD != null) {
        return;
    }
    TEST_WOOD = new blockTestWood();
    LASER_CASING = new BlockLaserCasing();
    LASER_PORT = new BlockLaserPort();
    SIMPLE_LASER_CONTROLLER = new BlockSimpleLaserController();
    PRIMITIVE_BULK_SMELTER = new BlockPrimitiveBulkSmelter();
    setupRegistryNames();
}

private static void setupRegistryNames() {
    TEST_WOOD.setRegistryName("test_wood");
    TEST_WOOD.setUnlocalizedName("gtinsanitycore.test_wood");
    LASER_CASING.setRegistryName("laser_casing");
    LASER_CASING.setUnlocalizedName("gtinsanitycore.laser_casing");
    LASER_PORT.setRegistryName("laser_port");
    LASER_PORT.setUnlocalizedName("gtinsanitycore.laser_port");
    SIMPLE_LASER_CONTROLLER.setRegistryName("simple_laser_controller");
    SIMPLE_LASER_CONTROLLER.setUnlocalizedName("gtinsanitycore.simple_laser_controller");
    PRIMITIVE_BULK_SMELTER.setRegistryName("primitive_bulk_smelter");
    PRIMITIVE_BULK_SMELTER.setUnlocalizedName("gtinsanitycore.primitive_bulk_smelter");
}

public static void registerBlocks(IForgeRegistry<Block> registry){
    init();
    registry.register(TEST_WOOD);
    registry.register(LASER_CASING);
    registry.register(LASER_PORT);
    registry.register(SIMPLE_LASER_CONTROLLER);
    registry.register(PRIMITIVE_BULK_SMELTER);
}

public static void registerItemBlocks(IForgeRegistry<Item> registry) {
    init();
    registerItemBlock(registry, TEST_WOOD);
    registerItemBlock(registry, LASER_CASING);
    registerItemBlock(registry, LASER_PORT);
    registerItemBlock(registry, SIMPLE_LASER_CONTROLLER);
    registerItemBlock(registry, PRIMITIVE_BULK_SMELTER);

}

private static void  registerItemBlock(@Nonnull IForgeRegistry<Item> registry, Block block){
    registry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
}

public static void registerTileEntities() {
    GameRegistry.registerTileEntity(TileEntityLaserPort.class,
            new ResourceLocation(GTInsanityCore.MODID, "laser_port"));
    GameRegistry.registerTileEntity(TileEntitySimpleLaserController.class,
            new ResourceLocation(GTInsanityCore.MODID, "simple_laser_controller"));
    GameRegistry.registerTileEntity(TileEntityPrimitiveBulkSmelter.class,
            new ResourceLocation(GTInsanityCore.MODID, "primitive_bulk_smelter"));
}}
