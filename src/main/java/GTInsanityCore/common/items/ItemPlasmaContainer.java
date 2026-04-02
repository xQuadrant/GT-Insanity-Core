package GTInsanityCore.common.items;

import GTInsanityCore.API.unification.GTIMaterials;
import GTInsanityCore.API.unification.flags.MaterialFlags;
import GTInsanityCore.API.unification.materials.IParticleMaterial;
import GTInsanityCore.API.unification.materials.InsanityMaterialRegistry;
import GTInsanityCore.GTInsanityCore;
import gregtech.api.GregTechAPI;
import gregtech.api.unification.material.Material;
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
import java.util.List;

public class ItemPlasmaContainer extends Item {

    public static final int CAPACITY = 1000;
    public static final String NBT_PLASMA_MATERIAL = "PlasmaMaterial";
    public static final String NBT_AMOUNT = "Amount";
    public static final String NBT_DECAY_TICKS = "DecayTicks";
    public static final String NBT_CONTAINMENT_TIME = "ContainmentTime";

    public ItemPlasmaContainer() {
        setMaxStackSize(16);
        setHasSubtypes(false);
        setCreativeTab(GTInsanityCore.TAB_INSANITY);
    }

    public static ItemStack createContainer(IParticleMaterial plasmaMaterial, int amount) {
        if (plasmaMaterial == null || !plasmaMaterial.hasPlasma()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(GTIItems.PLASMA_CONTAINER);
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setString(NBT_PLASMA_MATERIAL, plasmaMaterial.getName());
        nbt.setInteger(NBT_AMOUNT, Math.min(amount, CAPACITY));
        nbt.setLong(NBT_CONTAINMENT_TIME, System.currentTimeMillis());

        if (plasmaMaterial.hasFlag(MaterialFlags.INSTABLE_HALFLIFE)) {
            nbt.setInteger(NBT_DECAY_TICKS, calculateDecayTicks(plasmaMaterial));
        }

        stack.setTagCompound(nbt);
        return stack;
    }

    public static ItemStack createEmpty() {
        return new ItemStack(GTIItems.PLASMA_CONTAINER);
    }

    private static int calculateDecayTicks(IParticleMaterial material) {
        String name = material.getName();
        switch (name) {
            case "top_quark":
                return 20 * 5;
            case "w_boson_plus":
            case "w_boson_minus":
                return 20 * 10;
            case "z_boson":
                return 20 * 15;
            case "higgs_boson":
                return 20 * 20;
            case "muon":
                return 20 * 60 * 2;
            case "tau":
                return 20 * 60 * 5;
            default:
                return 20 * 60;
        }
    }

    @Nullable
    private static IParticleMaterial getDecayProduct(IParticleMaterial material) {
        String name = material.getName();
        switch (name) {
            case "top_quark":
                return GTIMaterials.BottomQuark;
            case "w_boson_plus":
                return GTIMaterials.Positron;
            case "w_boson_minus":
                return GTIMaterials.Electron;
            case "z_boson":
                return GTIMaterials.ElectronNeutrino;
            case "higgs_boson":
                return GTIMaterials.Photon;
            case "muon":
                return GTIMaterials.Electron;
            case "tau":
                return GTIMaterials.Muon;
            default:
                return null;
        }
    }

    @Override
    public void onUpdate(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int slot, boolean isSelected) {
        if (world.isRemote || !stack.hasTagCompound()) {
            return;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey(NBT_DECAY_TICKS)) {
            return;
        }

        int ticks = nbt.getInteger(NBT_DECAY_TICKS) - 1;
        if (ticks <= 0) {
            IParticleMaterial currentMat = getMaterial(stack);
            IParticleMaterial product = currentMat == null ? null : getDecayProduct(currentMat);
            int currentAmount = nbt.getInteger(NBT_AMOUNT);

            if (product != null && entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                ItemStack decayStack = createContainer(product, currentAmount / 2);
                player.inventory.setInventorySlotContents(slot, decayStack);
            }
        } else {
            nbt.setInteger(NBT_DECAY_TICKS, ticks);
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        items.add(createEmpty());

        IParticleMaterial[] plasmas = {
                GTIMaterials.UpQuark,
                GTIMaterials.DownQuark,
                GTIMaterials.CharmQuark,
                GTIMaterials.StrangeQuark,
                GTIMaterials.TopQuark,
                GTIMaterials.BottomQuark,
                GTIMaterials.Gluon,
                GTIMaterials.WBosonPlus,
                GTIMaterials.WBosonMinus,
                GTIMaterials.ZBoson,
                GTIMaterials.HiggsBoson
        };

        for (IParticleMaterial material : plasmas) {
            if (material != null) {
                items.add(createContainer(material, CAPACITY));
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
        IParticleMaterial material = getMaterial(stack);
        if (nbt == null || material == null) {
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.unknown"));
            return;
        }

        int amount = nbt.getInteger(NBT_AMOUNT);
        boolean isDecaying = nbt.hasKey(NBT_DECAY_TICKS);

        tooltip.add(I18n.format("gtinsanitycore.plasma_container.contains", material.getLocalizedName()));
        tooltip.add(I18n.format("gtinsanitycore.plasma_container.amount", amount, CAPACITY));

        double charge = getParticleCharge(material);
        if (charge != 0) {
            String chargeStr = charge > 0 ? "+" + charge : String.valueOf(charge);
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.charge", chargeStr));
        }

        if (isDecaying) {
            float seconds = nbt.getInteger(NBT_DECAY_TICKS) / 20.0f;
            tooltip.add("");
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.decay_warning", seconds));
        }

        if (material.hasFlag(MaterialFlags.COLOR_CONFINEMENT)) {
            tooltip.add("");
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.confinement.active"));
        }

        if (material.hasFlag(MaterialFlags.INSTABLE_HALFLIFE)) {
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.unstable"));
        }

        if (flag.isAdvanced()) {
            tooltip.add("");
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.material_id", material.getName()));
            tooltip.add(I18n.format("gtinsanitycore.plasma_container.contained_since",
                    nbt.getLong(NBT_CONTAINMENT_TIME)));
        }
    }

    @Nullable
    public static IParticleMaterial getMaterial(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return null;
        }

        String matName = stack.getTagCompound().getString(NBT_PLASMA_MATERIAL);
        IParticleMaterial material = InsanityMaterialRegistry.getMaterial(matName);
        if (material != null) {
            return material;
        }

        Material gtMaterial = GregTechAPI.materialManager.getMaterial(matName);
        return gtMaterial == null ? null : InsanityMaterialRegistry.wrapGTCEuMaterial(gtMaterial);
    }

    public static int getAmount(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return 0;
        }
        return stack.getTagCompound().getInteger(NBT_AMOUNT);
    }

    public static boolean isEmpty(ItemStack stack) {
        return !stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBT_PLASMA_MATERIAL);
    }

    private double getParticleCharge(IParticleMaterial material) {
        String name = material.getName();
        switch (name) {
            case "up_quark":
            case "charm_quark":
            case "top_quark":
            case "w_boson_plus":
                return 0.67;
            case "down_quark":
            case "strange_quark":
            case "bottom_quark":
            case "w_boson_minus":
                return -0.33;
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
        return !isEmpty(stack);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        return isEmpty(stack) ? ItemStack.EMPTY : createEmpty();
    }
}
