package dev.thorinwasher.blockutil.util;

import dev.thorinwasher.blockutil.BlockUtil;
import dev.thorinwasher.blockutil.api.event.BlockDisableDropEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;

import java.util.List;

public class BlockHelper {
    private BlockHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static List<Block> getBlockStructure(Block block) {
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Piston piston) {
            Block pistonHead = block.getRelative(piston.getFacing());
            if (pistonHead.getType() == Material.PISTON_HEAD) {
                return List.of(block, pistonHead);
            }
        }
        if (blockData instanceof PistonHead pistonHead) {
            Block piston = block.getRelative(pistonHead.getFacing().getOppositeFace());
            if (piston.getType() == Material.PISTON || piston.getType() == Material.STICKY_PISTON) {
                return List.of(block, piston);
            }
        }
        if (blockData instanceof Bed bed) {
            BlockFace relative = bed.getPart() == Bed.Part.HEAD ? bed.getFacing() : bed.getFacing().getOppositeFace();
            Block otherPart = block.getRelative(relative);
            if (otherPart.getBlockData() instanceof Bed otherBedPart && otherBedPart.getPart() != bed.getPart()
                    && otherBedPart.getFacing() == bed.getFacing()) {
                return List.of(block, otherPart);
            }
        }
        if (blockData instanceof Door door) {
            Block otherPart = door.getHalf() == Bisected.Half.BOTTOM ? block.getRelative(BlockFace.UP) : block.getRelative(BlockFace.DOWN);
            if (otherPart.getType() == blockData.getMaterial()) {
                return List.of(block, otherPart);
            }
        }
        return List.of(block);
    }

    public static void breakBlock(Block block, BlockUtil api) {
        BlockDisableDropEvent blockDisableDropEvent = api.newDisable(block);
        if (!blockDisableDropEvent.disableDrops()) {
            return;
        }
        if (block.getBlockData() instanceof Waterlogged waterlogged) {
            block.setType(waterlogged.isWaterlogged() ? Material.WATER : Material.AIR);
        } else {
            block.setType(Material.AIR);
        }
        Location location = block.getLocation().toCenterLocation();
        blockDisableDropEvent.getDropOverride()
                .forEach(itemStack -> block.getWorld().dropItemNaturally(location, itemStack));
        api.enableItemDrops(block);
    }

    public static void breakBlockIfTracked(Block block, BlockUtil api) {
        if (!api.blockItemDropsDisabled(block)) {
            return;
        }
        breakBlock(block, api);
    }

    public static List<Block> getAdjacentBlocks(Block block) {
        return List.of(block.getRelative(BlockFace.NORTH),
                block.getRelative(BlockFace.EAST),
                block.getRelative(BlockFace.SOUTH),
                block.getRelative(BlockFace.WEST));
    }
}
