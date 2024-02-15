package dev.thorinwasher.blockutil.api;

import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

public interface BlockUtilAPI {

    /**
     * Track this block, make it so that no items can be generated from breaking this block.
     * @param block <p>The block to track</p>
     */
    void disableItemDrops(Block block);

    /**
     * Free this block, make it so that items can be generated from breaking this block.
     * @param block <p>The block to free</p>
     */
    void enableItemDrops(Block block);

    /**
     * Check whether the block can drop items, i.e. if it has been tracked
     * @param block <p>The block to check if it's </p>
     * @return <p>True if the block is tracked</p>
     */
    boolean blockCanNotDropItems(Block block);

    /**
     * Move all tracking of a block to a new location
     * @param from <p>Original location</p>
     * @param delta <p>The difference the new location and original location</p>
     */
    void moveBlock(Block from, BlockVector delta);
}
