package dev.thorinwasher.noblockdrops.api;

import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

public interface NoBlockDropsAPI {

    void trackBlock(Block block);

    void freeBlock(Block block);

    boolean blockIsTracked(Block block);

    void moveBlock(Block from, BlockVector delta);
}
