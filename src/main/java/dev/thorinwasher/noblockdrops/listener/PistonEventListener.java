package dev.thorinwasher.noblockdrops.listener;

import dev.thorinwasher.noblockdrops.api.NoBlockDropsAPI;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.List;

public class PistonEventListener implements Listener {

    private final NoBlockDropsAPI api;

    public PistonEventListener(NoBlockDropsAPI api){
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockPistonExtend(BlockPistonExtendEvent event) {
        handlePistonEvent(event.getBlocks(), event.getDirection());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockPistonRetract(BlockPistonRetractEvent event) {
        handlePistonEvent(event.getBlocks(), event.getDirection());
    }

    private void handlePistonEvent(List<Block> blocks, BlockFace direction) {
        for (Block block : blocks) {
            api.moveBlock(block, direction.getDirection().toBlockVector());
        }
    }
}
