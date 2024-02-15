package dev.thorinwasher.blockutil.util;

import dev.thorinwasher.blockutil.api.BlockUtilAPI;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockHelper {

    private static final Set<Material> NEEDS_BLOCK_BELOW = getNeedsBlockBelow();

    private static final Set<Material> NEEDS_SUPPORT = getNeedsSupport();
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

    public static void breakBlock(Block block, BlockUtilAPI api) {
        block.setType(Material.AIR);
        api.freeBlock(block);
    }

    public static void breakBlockIfTracked(Block block, BlockUtilAPI api) {
        if (!api.blockIsTracked(block)) {
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

    public static boolean needsSupport(Material type){
        return NEEDS_SUPPORT.contains(type);
    }

    private static Set<Material> getNeedsSupport(){
        Set<Material> temp = new HashSet<>();
        temp.add(Material.CHORUS_FLOWER);
        temp.add(Material.CHORUS_PLANT);
        temp.add(Material.SCAFFOLDING);
        temp.addAll(Tag.CAVE_VINES.getValues());
        temp.add(Material.WEEPING_VINES);
        temp.add(Material.TWISTING_VINES);
        temp.addAll(Tag.SIGNS.getValues());
        temp.addAll(NEEDS_BLOCK_BELOW);
        temp.addAll(Tag.WALL_CORALS.getValues());
        temp.add(Material.DEAD_BRAIN_CORAL_WALL_FAN);
        temp.add(Material.DEAD_BUBBLE_CORAL_WALL_FAN);
        temp.add(Material.DEAD_FIRE_CORAL_WALL_FAN);
        temp.add(Material.DEAD_HORN_CORAL_WALL_FAN);
        temp.add(Material.WALL_TORCH);
        temp.add(Material.REDSTONE_WALL_TORCH);
        temp.add(Material.COCOA);
        temp.add(Material.LADDER);
        temp.add(Material.TRIPWIRE_HOOK);
        temp.addAll(Tag.WALL_SIGNS.getValues());
        temp.addAll(Tag.BUTTONS.getValues());
        temp.add(Material.LEVER);
        temp.add(Material.BELL);
        temp.addAll(Tag.BANNERS.getValues());
        return Set.copyOf(temp);
    }

    private static Set<Material> getNeedsBlockBelow() {
        Set<Material> temp = new HashSet<>();
        temp.add(Material.CAKE);
        temp.add(Material.COMPARATOR);
        temp.add(Material.REDSTONE_WIRE);
        temp.add(Material.REPEATER);
        temp.addAll(Tag.SAPLINGS.getValues());
        temp.add(Material.SEA_PICKLE);
        temp.addAll(Tag.STANDING_SIGNS.getValues());
        temp.add(Material.SNOW);
        temp.addAll(Tag.BANNERS.getValues());
        temp.addAll(Tag.FLOWER_POTS.getValues());
        temp.addAll(Tag.PRESSURE_PLATES.getValues());
        temp.addAll(Tag.DOORS.getValues());
        temp.addAll(Tag.CROPS.getValues());
        temp.addAll(Tag.RAILS.getValues());
        temp.addAll(Tag.TALL_FLOWERS.getValues());
        temp.addAll(Tag.WOOL_CARPETS.getValues());
        temp.add(Material.MOSS_CARPET);
        temp.add(Material.TORCH);
        temp.add(Material.REDSTONE_TORCH);
        temp.addAll(Tag.CORAL_PLANTS.getValues());
        temp.add(Material.DEAD_BRAIN_CORAL_FAN);
        temp.add(Material.DEAD_BUBBLE_CORAL_FAN);
        temp.add(Material.DEAD_FIRE_CORAL_FAN);
        temp.add(Material.DEAD_HORN_CORAL_FAN);
        return Set.copyOf(temp);
    }
}
