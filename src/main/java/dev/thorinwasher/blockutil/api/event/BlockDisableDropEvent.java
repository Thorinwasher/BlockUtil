package dev.thorinwasher.blockutil.api.event;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public class BlockDisableDropEvent {

    private final BlockState block;
    private boolean disableDrops = true;
    private List<ItemStack> dropOverride = List.of();

    @ApiStatus.Internal
    public BlockDisableDropEvent(BlockState block) {
        this.block = block;
    }

    public BlockState getBlock() {
        return block;
    }

    public List<ItemStack> getDropOverride() {
        return dropOverride;
    }

    public boolean disableDrops() {
        return disableDrops;
    }

    public void setDropOverride(List<ItemStack> dropOverride) {
        this.dropOverride = dropOverride;
    }

    public void setDisableDrops(boolean disableDrops) {
        this.disableDrops = disableDrops;
    }
}
