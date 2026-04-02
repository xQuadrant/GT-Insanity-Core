package GTInsanityCore.client;

import GTInsanityCore.GTInsanityCore;
import GTInsanityCore.API.unification.GTIMaterials;
import GTInsanityCore.common.items.ItemPlasmaContainer;
import gregtech.api.unification.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class GTICreativeTabs {

    public static CreativeTabs TAB_INSANITY;

    public static void init() {
        TAB_INSANITY = new CreativeTabs(GTInsanityCore.MODID + ".particles") {
            @Override
            public ItemStack getTabIconItem() {
                return ItemPlasmaContainer.createContainer((Material) GTIMaterials.HiggsBoson, 1000);
            }

            @Override
            public void displayAllRelevantItems(@Nonnull NonNullList<ItemStack> items) {
                // Quarks
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.UpQuark, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.DownQuark, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.CharmQuark, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.StrangeQuark, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.TopQuark, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.BottomQuark, 1000));

                // Leptons
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.Electron, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.Muon, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.Tau, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.ElectronNeutrino, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.MuonNeutrino, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.TauNeutrino, 1000));

                // Bosons
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.Photon, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.Gluon, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.WBosonPlus, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.WBosonMinus, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.ZBoson, 1000));
                items.add(ItemPlasmaContainer.createContainer((Material) GTIMaterials.HiggsBoson, 1000));

                // Empty container
                items.add(ItemPlasmaContainer.createEmpty());
            }
        };
    }
}