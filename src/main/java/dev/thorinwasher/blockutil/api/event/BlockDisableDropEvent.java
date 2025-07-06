package dev.thorinwasher.blockutil.api.event;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

public class BlockDisableDropEvent {

    private final Block block;
    private boolean disableDrops = false;
    private ItemStack dropOverride;

    @ApiStatus.Internal
    public BlockDisableDropEvent(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public ItemStack getDropOverride() {
        return dropOverride;
    }

    public boolean getDisableDrops() {
        return disableDrops;
    }

    public void setDropOverride(ItemStack dropOverride) {
        this.dropOverride = dropOverride;
    }

    public void setDisableDrops(boolean disableDrops) {
        this.disableDrops = disableDrops;
    }
}
