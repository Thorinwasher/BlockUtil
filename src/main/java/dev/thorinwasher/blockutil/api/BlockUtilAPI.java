package dev.thorinwasher.blockutil.api;

import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

public interface BlockUtilAPI {

    void trackBlock(Block block);

    void freeBlock(Block block);

    boolean blockIsTracked(Block block);

    void moveBlock(Block from, BlockVector delta);
}
