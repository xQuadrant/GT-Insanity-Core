package GTInsanityCore.common.metatileentities;

import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MetaTileEntityPrimitiveBulkSmelterMultiblock extends MultiblockWithDisplayBase implements IControllable {

    private static final String[] BACK_SLICE = {"XXX", "XXX", "CCC"};
    private static final String[] MIDDLE_SLICE = {"XXX", "X#X", "CCC"};
    private static final String[] FRONT_SLICE = {"XXX", "XSX", "CCC"};

    private static final String NBT_WORKING_ENABLED = "WorkingEnabled";
    private static final String NBT_ACTIVE = "Active";
    private static final String NBT_PROGRESS = "Progress";
    private static final String NBT_COMPLETED_CYCLES = "CompletedCycles";
    private static final int MAX_PROGRESS = 100;

    private final ItemStackHandler displayInventory = new ItemStackHandler(2);

    private boolean workingEnabled = true;
    private boolean active = false;
    private int progress = 0;
    private int completedCycles = 0;

    public MetaTileEntityPrimitiveBulkSmelterMultiblock(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityPrimitiveBulkSmelterMultiblock(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle(BACK_SLICE)
                .aisle(MIDDLE_SLICE)
                .aisle(FRONT_SLICE)
                .where('S', selfPredicate())
                .where('C', states(getCokeBrickState()).setMinGlobalLimited(9))
                .where('X', states(getTerracottaState()).setMinGlobalLimited(11)
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setExactLimit(2).setPreviewCount(2))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setExactLimit(1).setPreviewCount(1)))
                .where('#', air())
                .build();
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        return Collections.singletonList(MultiblockShapeInfo.builder()
                .aisle(BACK_SLICE)
                .aisle(MIDDLE_SLICE)
                .aisle("XOX", "ISI", "CCC")
                .where('S', this, EnumFacing.NORTH)
                .where('C', getCokeBrickState())
                .where('X', getTerracottaState())
                .where('#', net.minecraft.init.Blocks.AIR.getDefaultState())
                .where('I', MetaTileEntities.ITEM_IMPORT_BUS[0], EnumFacing.NORTH)
                .where('O', MetaTileEntities.ITEM_EXPORT_BUS[0], EnumFacing.NORTH)
                .build());
    }

    @Override
    protected void updateFormedValid() {
        if (!workingEnabled) {
            active = false;
            resetProgress();
            return;
        }

        SmeltingTarget target = findSmeltingTarget();
        if (target == null) {
            active = false;
            resetProgress();
            return;
        }

        setDisplayStacks(target.inputPreview, target.outputPreview);
        active = true;
        progress++;

        if (progress >= MAX_PROGRESS) {
            progress = 0;
            if (commitSmelt(target)) {
                completedCycles++;
            } else {
                active = false;
            }
        }

        markDirty();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        active = false;
        resetProgress();
        setDisplayStacks(ItemStack.EMPTY, ItemStack.EMPTY);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(active, workingEnabled)
                .addCustom(lines -> {
                    lines.add(new TextComponentString("Progress: " + progress + "/" + MAX_PROGRESS));
                    lines.add(new TextComponentString("Completed Cycles: " + completedCycles));
                    ItemStack currentInput = displayInventory.getStackInSlot(0);
                    if (!currentInput.isEmpty()) {
                        lines.add(new TextComponentString("Smelting: " + currentInput.getDisplayName()));
                    }
                });
        super.addDisplayText(textList);
    }

    @Override
    protected ModularUI.Builder createUITemplate(EntityPlayer player) {
        return ModularUI.builder(GuiTextures.PRIMITIVE_BACKGROUND, 176, 166)
                .shouldColor(false)
                .widget(new LabelWidget(10, 8, "gtinsanitycore.machine.primitive_bulk_smelter.name"))
                .widget(new SlotWidget(displayInventory, 0, 52, 35, false, false)
                        .setBackgroundTexture(GuiTextures.PRIMITIVE_SLOT, GuiTextures.PRIMITIVE_FURNACE_OVERLAY))
                .widget(new ProgressWidget(this::getProgressPercent, 79, 34, 20, 15,
                        GuiTextures.PROGRESS_BAR_ARROW, ProgressWidget.MoveType.HORIZONTAL))
                .widget(new SlotWidget(displayInventory, 1, 107, 35, false, false)
                        .setBackgroundTexture(GuiTextures.PRIMITIVE_SLOT, GuiTextures.PRIMITIVE_INGOT_OVERLAY))
                .dynamicLabel(52, 58, this::getCurrentItemName, 0x404040)
                .bindPlayerInventory(player.inventory, GuiTextures.PRIMITIVE_SLOT, 0);
    }

    @Override
    public ICubeRenderer getBaseTexture(gregtech.api.metatileentity.multiblock.IMultiblockPart sourcePart) {
        return Textures.PRIMITIVE_BRICKS;
    }

    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.COKE_OVEN_OVERLAY;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        this.workingEnabled = workingEnabled;
        markDirty();
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE) {
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean(NBT_WORKING_ENABLED, workingEnabled);
        data.setBoolean(NBT_ACTIVE, active);
        data.setInteger(NBT_PROGRESS, progress);
        data.setInteger(NBT_COMPLETED_CYCLES, completedCycles);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        workingEnabled = data.getBoolean(NBT_WORKING_ENABLED);
        active = data.getBoolean(NBT_ACTIVE);
        progress = data.getInteger(NBT_PROGRESS);
        completedCycles = data.getInteger(NBT_COMPLETED_CYCLES);
    }

    @Override
    public String[] getDescription() {
        return new String[] {"gtinsanitycore.machine.primitive_bulk_smelter.tooltip"};
    }

    private void resetProgress() {
        if (progress != 0) {
            progress = 0;
            markDirty();
        }
    }

    private double getProgressPercent() {
        return MAX_PROGRESS <= 0 ? 0.0D : (double) progress / (double) MAX_PROGRESS;
    }

    private String getCurrentItemName() {
        ItemStack stack = displayInventory.getStackInSlot(0);
        return stack.isEmpty() ? "Idle" : stack.getDisplayName();
    }

    private void setDisplayStacks(ItemStack input, ItemStack output) {
        displayInventory.setStackInSlot(0, input);
        displayInventory.setStackInSlot(1, output);
    }

    private SmeltingTarget findSmeltingTarget() {
        List<IItemHandlerModifiable> imports = getAbilities(MultiblockAbility.IMPORT_ITEMS);
        List<IItemHandlerModifiable> exports = getAbilities(MultiblockAbility.EXPORT_ITEMS);
        if (imports.isEmpty() || exports.isEmpty()) {
            return null;
        }

        for (IItemHandlerModifiable inputBus : imports) {
            for (int slot = 0; slot < inputBus.getSlots(); slot++) {
                ItemStack input = inputBus.getStackInSlot(slot);
                if (input.isEmpty()) {
                    continue;
                }

                ItemStack output = FurnaceRecipes.instance().getSmeltingResult(input);
                if (output.isEmpty()) {
                    continue;
                }

                if (canInsert(exports, output.copy())) {
                    return new SmeltingTarget(input.copy(), output.copy());
                }
            }
        }
        return null;
    }

    private boolean commitSmelt(SmeltingTarget target) {
        List<IItemHandlerModifiable> imports = getAbilities(MultiblockAbility.IMPORT_ITEMS);
        List<IItemHandlerModifiable> exports = getAbilities(MultiblockAbility.EXPORT_ITEMS);
        if (imports.isEmpty() || exports.isEmpty() || !canInsert(exports, target.outputPreview.copy())) {
            return false;
        }

        for (IItemHandlerModifiable inputBus : imports) {
            for (int slot = 0; slot < inputBus.getSlots(); slot++) {
                ItemStack input = inputBus.getStackInSlot(slot);
                if (input.isEmpty() || !ItemStack.areItemsEqual(input, target.inputPreview)) {
                    continue;
                }

                ItemStack result = FurnaceRecipes.instance().getSmeltingResult(input);
                if (result.isEmpty()) {
                    continue;
                }

                inputBus.extractItem(slot, 1, false);
                insert(exports, result.copy());
                setDisplayStacks(input.copy(), result.copy());
                return true;
            }
        }

        return false;
    }

    private boolean canInsert(List<IItemHandlerModifiable> outputs, ItemStack stack) {
        for (IItemHandlerModifiable outputBus : outputs) {
            ItemStack remaining = stack.copy();
            for (int slot = 0; slot < outputBus.getSlots(); slot++) {
                remaining = outputBus.insertItem(slot, remaining, true);
                if (remaining.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void insert(List<IItemHandlerModifiable> outputs, ItemStack stack) {
        ItemStack remaining = stack;
        for (IItemHandlerModifiable outputBus : outputs) {
            for (int slot = 0; slot < outputBus.getSlots(); slot++) {
                remaining = outputBus.insertItem(slot, remaining, false);
                if (remaining.isEmpty()) {
                    return;
                }
            }
        }
    }

    private static IBlockState getCokeBrickState() {
        Block block = Block.getBlockFromName("gregtech:metal_casing");
        return block == null ? net.minecraft.init.Blocks.BRICK_BLOCK.getDefaultState() : block.getStateFromMeta(8);
    }

    private static IBlockState getTerracottaState() {
        return net.minecraft.init.Blocks.HARDENED_CLAY.getDefaultState();
    }

    private static final class SmeltingTarget {
        private final ItemStack inputPreview;
        private final ItemStack outputPreview;

        private SmeltingTarget(ItemStack inputPreview, ItemStack outputPreview) {
            this.inputPreview = inputPreview;
            this.outputPreview = outputPreview;
        }
    }
}
