package GTInsanityCore.common.items;

import GTInsanityCore.API.unification.materials.IParticleMaterial;
import GTInsanityCore.GTInsanityCore;
import GTInsanityCore.API.unification.GTIMaterials;
import GTInsanityCore.API.unification.flags.MaterialFlags;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.registry.MaterialRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PropertyKey;
import java.util.List;

public class ItemPlasmaContainer extends Item {

    public static final int CAPACITY = 1000; // mB equivalent
    public static final String NBT_PLASMA_MATERIAL = "PlasmaMaterial";
    public static final String NBT_AMOUNT = "Amount";
    public static final String NBT_DECAY_TICKS = "DecayTicks";
    public static final String NBT_CONTAINMENT_TIME = "ContainmentTime";

    public ItemPlasmaContainer() {
        setMaxStackSize(16);
        setHasSubtypes(false);
        setCreativeTab(GTInsanityCore.TAB_INSANITY);
    }

    /**
     * Creates a filled plasma container
     */
    public static ItemStack createContainer(Material plasmaMaterial, int amount) {
        if (plasmaMaterial == null || !plasmaMaterial.hasProperty(PropertyKey.PLASMA)) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(GTIItems.PLASMA_CONTAINER);
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setString(NBT_PLASMA_MATERIAL, plasmaMaterial.getName());
        nbt.setInteger(NBT_AMOUNT, Math.min(amount, CAPACITY));
        nbt.setLong(NBT_CONTAINMENT_TIME, System.currentTimeMillis());

        // Calculate decay time for unstable particles
        if (plasmaMaterial.hasFlag(MaterialFlags.INSTABLE_HALFLIFE)) {
            int decayTicks = calculateDecayTicks(plasmaMaterial);
            nbt.setInteger(NBT_DECAY_TICKS, decayTicks);
        }

        stack.setTagCompound(nbt);
        return stack;
    }

    /**
     * Creates an empty container
     */
    public static ItemStack createEmpty() {
        return new ItemStack(GTIItems.PLASMA_CONTAINER);
    }

    /**
     * Calculate decay time based on particle type (in ticks)
     */
    private static int calculateDecayTicks(Material material) {
        String name = material.getName();

        // Realistic-ish half-lives scaled for gameplay
        switch (name) {
            case "top_quark": return 20 * 5;      // 5 seconds (very unstable)
            case "w_boson_plus":
            case "w_boson_minus": return 20 * 10;   // 10 seconds
            case "z_boson": return 20 * 15;       // 15 seconds
            case "higgs_boson": return 20 * 20;   // 20 seconds (relatively stable)
            case "muon": return 20 * 60 * 2;      // 2 minutes
            case "tau": return 20 * 60 * 5;       // 5 minutes
            default: return 20 * 60;              // 1 minute default
        }
    }

    /**
     * Get decay product for a material
     */
    private static Material getDecayProduct(IParticleMaterial material) {
        String name = material.getName();

        switch (name) {
            case "top_quark": return (Material) GTIMaterials.BottomQuark; // t → b + W+
            case "w_boson_plus": return (Material) GTIMaterials.Positron; // W+ → e+ + νe
            case "w_boson_minus": return (Material) GTIMaterials.Electron; // W- → e- + ν̄e
            case "z_boson": return (Material) GTIMaterials.ElectronNeutrino; // Z → ν + ν̄
            case "higgs_boson": return (Material) GTIMaterials.Photon; // H → γ + γ (simplified)
            case "muon": return (Material) GTIMaterials.Electron; // μ → e + νμ + ν̄e
            case "tau": return (Material) GTIMaterials.Muon; // τ → μ + ντ + ν̄μ
            default: return null;
        }
    }

    @Override
    public void onUpdate(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int slot, boolean isSelected) {
        if (world.isRemote || !stack.hasTagCompound()) return;

        NBTTagCompound nbt = stack.getTagCompound();

        // Handle decay
        assert nbt != null;
        if (nbt.hasKey(NBT_DECAY_TICKS)) {
            int ticks = nbt.getInteger(NBT_DECAY_TICKS) - 1;

            if (ticks <= 0) {
                // Decay occurred!
                Material currentMat = getMaterial(stack);
                assert currentMat != null;
                Material product = getDecayProduct((IParticleMaterial) currentMat);
                int currentAmount = nbt.getInteger(NBT_AMOUNT);

                if (product != null && entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;

                    // Create decay product container (half amount for balance)
                    ItemStack decayStack = createContainer(product, currentAmount / 2);

                    // Replace current stack
                    player.inventory.setInventorySlotContents(slot, decayStack);

                    // Optional: spawn neutrinos (gas) as byproduct
                    if (world.rand.nextFloat() < 0.5f) {
                        // Could spawn actual gas in world or give player
                    }
                }
            } else {
                nbt.setInteger(NBT_DECAY_TICKS, ticks);
            }
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) return;

        // Add empty container
        items.add(createEmpty());

        // Add filled containers for all plasma materials
        Material[] plasmas = {
                (Material) GTIMaterials.UpQuark,
                (Material) GTIMaterials.DownQuark,
                (Material) GTIMaterials.CharmQuark,
                (Material) GTIMaterials.StrangeQuark,
                (Material) GTIMaterials.TopQuark,
                (Material) GTIMaterials.BottomQuark,
                (Material) GTIMaterials.Gluon,
                (Material) GTIMaterials.WBosonPlus,
                (Material) GTIMaterials.WBosonMinus,
                (Material) GTIMaterials.ZBoson,
                (Material) GTIMaterials.HiggsBoson
        };

        for (Material mat : plasmas) {
            if (mat != null) {
                items.add(createContainer(mat, CAPACITY));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBT_PLASMA_MATERIAL)) {
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.empty"));
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.empty.hint"));
            return;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        Material material = getMaterial(stack);

        if (material == null) {
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.unknown"));
            return;
        }

        int amount = nbt.getInteger(NBT_AMOUNT);
        boolean isDecaying = nbt.hasKey(NBT_DECAY_TICKS);

        // Plasma type
        tooltip.add(I18n.format("gtinsanitycore.plasma_container.contains",
                material.getLocalizedName()));

        // Amount bar
        tooltip.add(I18n.format("gtinsanitycore.plasma_container.amount",
                amount, CAPACITY));

        // Charge display
        double charge = getParticleCharge(material);
        if (charge != 0) {
            String chargeStr = charge > 0 ? "+" + charge : String.valueOf(charge);
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.charge", chargeStr));
        }

        // Decay warning
        if (isDecaying) {
            int ticks = nbt.getInteger(NBT_DECAY_TICKS);
            float seconds = ticks / 20.0f;
            tooltip.add("");
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.decay_warning", seconds));
        }

        // Confinement status
        if (material.hasFlag(MaterialFlags.COLOR_CONFINEMENT)) {
            tooltip.add("");
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.confinement.active"));
        }

        // Stability indicator
        if (material.hasFlag(MaterialFlags.INSTABLE_HALFLIFE)) {
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.unstable"));
        }

        // Advanced info (shift key)
        if (flag.isAdvanced()) {
            tooltip.add("");
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.material_id",
                    material.getName()));
            long containmentTime = nbt.getLong(NBT_CONTAINMENT_TIME);
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.contained_since",
                    containmentTime));
        }
    }

    /**
     * Get material from stack NBT
     */
    @Nullable
    public static Material getMaterial(ItemStack stack) {
        if (!stack.hasTagCompound()) return null;
        String matName = stack.getTagCompound().getString(NBT_PLASMA_MATERIAL);
        return MaterialRegistry.getMaterial(matName);
    }

    /**
     * Get amount from stack NBT
     */
    public static int getAmount(ItemStack stack) {
        if (!stack.hasTagCompound()) return 0;
        return stack.getTagCompound().getInteger(NBT_AMOUNT);
    }

    /**
     * Check if container is empty
     */
    public static boolean isEmpty(ItemStack stack) {
        return !stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBT_PLASMA_MATERIAL);
    }

    /**
     * Get particle charge for display (simplified)
     */
    private double getParticleCharge(Material material) {
        String name = material.getName();
        switch (name) {
            case "up_quark":
            case "charm_quark":
            case "top_quark":
            case "w_boson_plus":
                return 0.67; // +2/3 rounded

            case "down_quark":
            case "strange_quark":
            case "bottom_quark":
            case "w_boson_minus":
                return -0.33; // -1/3 rounded

            case "electron":
            case "muon":
            case "tau":
                return -1.0;

            default:
                return 0.0;
        }
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        // Return empty container when used in crafting
        return !isEmpty(stack);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        return isEmpty(stack) ? ItemStack.EMPTY : createEmpty();
    }
}
