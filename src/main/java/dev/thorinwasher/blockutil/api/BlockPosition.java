package dev.thorinwasher.blockutil.api;

import org.bukkit.Location;
import org.bukkit.util.BlockVector;

public record BlockPosition(int x, int y, int z) {

    public static BlockPosition from(Location location) {
        return new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Does not mutate the object, returns a new object
     *
     * @param delta <p>The vector to add</p>
     * @return <p>The new block location</p>
     */
    public BlockPosition add(BlockVector delta) {
        return new BlockPosition(x + delta.getBlockX(), y + delta.getBlockY(), z + delta.getBlockZ());
    }
}
