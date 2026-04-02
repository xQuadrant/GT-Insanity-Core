package GTInsanityCore.client;

import GTInsanityCore.API.unification.GTIMaterials;
import GTInsanityCore.API.unification.materials.IParticleMaterial;
import GTInsanityCore.GTInsanityCore;
import GTInsanityCore.common.items.ItemPlasmaContainer;
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
                return ItemPlasmaContainer.createContainer(GTIMaterials.HiggsBoson, 1000);
            }

            @Override
            public void displayAllRelevantItems(@Nonnull NonNullList<ItemStack> items) {
                addIfPresent(items, GTIMaterials.UpQuark);
                addIfPresent(items, GTIMaterials.DownQuark);
                addIfPresent(items, GTIMaterials.CharmQuark);
                addIfPresent(items, GTIMaterials.StrangeQuark);
                addIfPresent(items, GTIMaterials.TopQuark);
                addIfPresent(items, GTIMaterials.BottomQuark);

                addIfPresent(items, GTIMaterials.Electron);
                addIfPresent(items, GTIMaterials.Muon);
                addIfPresent(items, GTIMaterials.Tau);
                addIfPresent(items, GTIMaterials.ElectronNeutrino);
                addIfPresent(items, GTIMaterials.MuonNeutrino);
                addIfPresent(items, GTIMaterials.TauNeutrino);

                addIfPresent(items, GTIMaterials.Photon);
                addIfPresent(items, GTIMaterials.Gluon);
                addIfPresent(items, GTIMaterials.WBosonPlus);
                addIfPresent(items, GTIMaterials.WBosonMinus);
                addIfPresent(items, GTIMaterials.ZBoson);
                addIfPresent(items, GTIMaterials.HiggsBoson);

                items.add(ItemPlasmaContainer.createEmpty());
            }
        };
    }

    private static void addIfPresent(NonNullList<ItemStack> items, IParticleMaterial material) {
        if (material != null) {
            items.add(ItemPlasmaContainer.createContainer(material, 1000));
        }
    }
}
