package GTInsanityCore.common.items;

import GTInsanityCore.GTInsanityCore;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class GTIItems {

    // Particle containment items
    public static ItemPlasmaContainer PLASMA_CONTAINER;
    public static Item ELECTRIC_MOTOR_ULV;
    public static Item ELECTRIC_PUMP_ULV;
    public static Item CONVEYOR_MODULE_ULV;
    public static Item ELECTRIC_PISTON_ULV;
    public static Item ROBOT_ARM_ULV;
    public static Item FIELD_GENERATOR_ULV;
    public static Item EMITTER_ULV;
    public static Item SENSOR_ULV;
    public static Item FLUID_REGULATOR_ULV;
    //public static ItemStrata STRATA_ITEM;
    //public static ItemResearchToken RESEARCH_TOKEN;

    public static void init() {
        if (PLASMA_CONTAINER == null) {
            PLASMA_CONTAINER = createItem("plasma_container", new ItemPlasmaContainer());
            ELECTRIC_MOTOR_ULV = createItem("electric_motor_ulv", new Item());
            ELECTRIC_PUMP_ULV = createItem("electric_pump_ulv", new Item());
            CONVEYOR_MODULE_ULV = createItem("conveyor_module_ulv", new Item());
            ELECTRIC_PISTON_ULV = createItem("electric_piston_ulv", new Item());
            ROBOT_ARM_ULV = createItem("robot_arm_ulv", new Item());
            FIELD_GENERATOR_ULV = createItem("field_generator_ulv", new Item());
            EMITTER_ULV = createItem("emitter_ulv", new Item());
            SENSOR_ULV = createItem("sensor_ulv", new Item());
            FLUID_REGULATOR_ULV = createItem("fluid_regulator_ulv", new Item());
        }
        //STRATA_ITEM = createItem("strata", new ItemStrata());
        //RESEARCH_TOKEN = createItem("research_token", new ItemResearchToken());
    }

    private static <T extends Item> T createItem(String name, T item) {
        item.setRegistryName(GTInsanityCore.MODID, name);
        item.setUnlocalizedName(GTInsanityCore.MODID + "." + name);
        item.setCreativeTab(GTInsanityCore.TAB_INSANITY); // Create this creative tab!
        return item;
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        init();
        registry.register(PLASMA_CONTAINER);
        registry.register(ELECTRIC_MOTOR_ULV);
        registry.register(ELECTRIC_PUMP_ULV);
        registry.register(CONVEYOR_MODULE_ULV);
        registry.register(ELECTRIC_PISTON_ULV);
        registry.register(ROBOT_ARM_ULV);
        registry.register(FIELD_GENERATOR_ULV);
        registry.register(EMITTER_ULV);
        registry.register(SENSOR_ULV);
        registry.register(FLUID_REGULATOR_ULV);
    }
}
