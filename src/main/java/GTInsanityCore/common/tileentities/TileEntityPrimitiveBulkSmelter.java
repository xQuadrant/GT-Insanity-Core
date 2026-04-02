package GTInsanityCore.common.tileentities;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class TileEntityPrimitiveBulkSmelter extends AbstractPatternedMultiblockTileEntity {

    private static final String NBT_PROGRESS = "Progress";
    private static final String NBT_CYCLES = "CompletedCycles";
    private static final int MAX_PROGRESS = 100;

    private int progress;
    private int completedCycles;

    @Override
    protected void updateFormed() {
        if (tryProcessSmeltingTick()) {
            progress++;
            if (progress >= MAX_PROGRESS) {
                progress = 0;
                completedCycles++;
            }
            markDirty();
        } else if (progress > 0) {
            progress = 0;
            markDirty();
        }
    }

    @Override
    protected void updateUnformed() {
        if (progress != 0) {
            progress = 0;
            markDirty();
        }
    }

    public String getDebugStatus() {
        return formatStructureState("Primitive Bulk Smelter") + " "
                + "Progress=" + progress + "/" + MAX_PROGRESS
                + ", Cycles=" + completedCycles;
    }

    @Override
    protected boolean checkStructurePattern() {
        if (world == null) {
            return false;
        }

        int inputBuses = 0;
        int outputBuses = 0;

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos target = pos.add(dx, dy, dz);
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }

                    if (dy == -1) {
                        if (!isCokeOvenBrick(target)) {
                            return false;
                        }
                        continue;
                    }

                    if (isInputBus(target)) {
                        inputBuses++;
                        continue;
                    }
                    if (isOutputBus(target)) {
                        outputBuses++;
                        continue;
                    }
                    if (!isTerracotta(target)) {
                        return false;
                    }
                }
            }
        }
        return inputBuses == 2 && outputBuses == 1;
    }

    private boolean tryProcessSmeltingTick() {
        IItemHandlerModifiable output = getOutputBusHandler();
        if (output == null) {
            return false;
        }

        BlockPos[] inputs = getInputBusPositions();
        for (BlockPos inputPos : inputs) {
            IItemHandlerModifiable input = getItemHandler(inputPos);
            if (input == null) {
                continue;
            }

            for (int slot = 0; slot < input.getSlots(); slot++) {
                ItemStack stack = input.getStackInSlot(slot);
                if (stack.isEmpty()) {
                    continue;
                }

                ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
                if (result.isEmpty()) {
                    continue;
                }

                if (!canOutput(output, result)) {
                    return false;
                }

                input.extractItem(slot, 1, false);
                insertIntoOutput(output, result.copy());
                return true;
            }
        }
        return false;
    }

    private boolean canOutput(IItemHandler output, ItemStack result) {
        ItemStack remaining = result.copy();
        for (int slot = 0; slot < output.getSlots(); slot++) {
            remaining = output.insertItem(slot, remaining, true);
            if (remaining.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void insertIntoOutput(IItemHandlerModifiable output, ItemStack result) {
        ItemStack remaining = result;
        for (int slot = 0; slot < output.getSlots(); slot++) {
            remaining = output.insertItem(slot, remaining, false);
            if (remaining.isEmpty()) {
                return;
            }
        }
    }

    private IItemHandlerModifiable getOutputBusHandler() {
        for (int dy = 0; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos target = pos.add(dx, dy, dz);
                    if (isOutputBus(target)) {
                        return getItemHandler(target);
                    }
                }
            }
        }
        return null;
    }

    private BlockPos[] getInputBusPositions() {
        BlockPos[] positions = new BlockPos[2];
        int index = 0;
        for (int dy = 0; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos target = pos.add(dx, dy, dz);
                    if (isInputBus(target) && index < positions.length) {
                        positions[index++] = target;
                    }
                }
            }
        }
        return positions;
    }

    private IItemHandlerModifiable getItemHandler(BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (handler instanceof IItemHandlerModifiable) {
                return (IItemHandlerModifiable) handler;
            }
        }
        return null;
    }

    private boolean isCokeOvenBrick(BlockPos target) {
        Block block = world.getBlockState(target).getBlock();
        return block == Block.getBlockFromName("gregtech:metal_casing")
                && block.getMetaFromState(world.getBlockState(target)) == 8;
    }

    private boolean isTerracotta(BlockPos target) {
        return world.getBlockState(target).getBlock() == Blocks.HARDENED_CLAY;
    }

    private boolean isInputBus(BlockPos target) {
        return isSpecificItemBus(target, MetaTileEntities.ITEM_IMPORT_BUS[0]);
    }

    private boolean isOutputBus(BlockPos target) {
        return isSpecificItemBus(target, MetaTileEntities.ITEM_EXPORT_BUS[0]);
    }

    private boolean isSpecificItemBus(BlockPos target, MetaTileEntity expected) {
        TileEntity tileEntity = world.getTileEntity(target);
        if (tileEntity == null || expected == null) {
            return false;
        }
        MetaTileEntity metaTileEntity = extractMetaTileEntity(tileEntity);
        if (metaTileEntity == null) {
            return false;
        }
        return expected.metaTileEntityId.equals(metaTileEntity.metaTileEntityId);
    }

    private MetaTileEntity extractMetaTileEntity(TileEntity tileEntity) {
        try {
            Object result = tileEntity.getClass().getMethod("getMetaTileEntity").invoke(tileEntity);
            if (result instanceof MetaTileEntity) {
                return (MetaTileEntity) result;
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(NBT_PROGRESS, progress);
        compound.setInteger(NBT_CYCLES, completedCycles);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        progress = compound.getInteger(NBT_PROGRESS);
        completedCycles = compound.getInteger(NBT_CYCLES);
    }
}
