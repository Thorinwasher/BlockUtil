package dev.thorinwasher.noblockdrops.listener;

import dev.thorinwasher.noblockdrops.api.NoBlockDropsAPI;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import java.util.List;

public class BlockEventListener implements Listener {

    private final NoBlockDropsAPI api;

    public BlockEventListener(NoBlockDropsAPI api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onBlockDropItem(BlockDropItemEvent event) {
        if (api.blockIsTracked(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockBreak(BlockBreakEvent event) {
        api.freeBlock(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockBurn(BlockBurnEvent event) {
        api.freeBlock(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockFade(BlockFadeEvent event) {
        api.freeBlock(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockForm(BlockFormEvent event) {
        api.freeBlock(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockPlace(BlockPlaceEvent event) {
        api.freeBlock(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onLeavesDecay(LeavesDecayEvent event) {
        api.freeBlock(event.getBlock());
    }


}
