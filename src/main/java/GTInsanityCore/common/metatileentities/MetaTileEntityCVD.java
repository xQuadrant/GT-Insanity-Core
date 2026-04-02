package GTInsanityCore.common.metatileentities;

import GTInsanityCore.common.recipes.GTIRecipeMaps;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;
import net.minecraft.util.ResourceLocation;

public class MetaTileEntityCVD extends SimpleMachineMetaTileEntity {

    private final int tier;

    public MetaTileEntityCVD(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, GTIRecipeMaps.CVD_RECIPES, Textures.CHEMICAL_REACTOR_OVERLAY, tier, true);
        this.tier = tier;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityCVD(metaTileEntityId, tier);
    }
}
