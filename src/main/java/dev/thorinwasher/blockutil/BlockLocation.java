package dev.thorinwasher.blockutil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.BlockVector;

import java.util.UUID;

public class BlockLocation {

    private final UUID world;
    private final int x;
    private final int y;
    private final int z;

    public BlockLocation(int x, int y, int z, UUID world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public BlockLocation(Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getUID());
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public UUID getWorld() {
        return world;
    }

    /**
     * Does not mutate the object, returns a new object
     * @param delta <p>The vector to add</p>
     * @return <p>The new block location</p>
     */
    public BlockLocation add(BlockVector delta) {
        return new BlockLocation(x + delta.getBlockX(), y + delta.getBlockY(), z + delta.getBlockZ(), world);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BlockLocation otherBlock)) {
            return false;
        }
        return otherBlock.x == this.x && otherBlock.y == this.y && otherBlock.z == this.z && otherBlock.world.equals(this.world);
    }

    @Override
    public int hashCode() {
        int result = 18;

        result = result * 27 + x;
        result = result * 27 + y;
        result = result * 27 + z;
        result = result * 27 + world.hashCode();

        return result;
    }
}
