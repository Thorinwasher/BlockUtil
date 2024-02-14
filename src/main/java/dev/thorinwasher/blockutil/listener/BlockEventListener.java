package dev.thorinwasher.blockutil.listener;

import dev.thorinwasher.blockutil.BlockUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.util.BlockVector;

public class BlockEventListener implements Listener {

    private final BlockUtil api;

    public BlockEventListener(BlockUtil api) {
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
    void onBlockFromTo(BlockFromToEvent event) {
        if (event.getBlock().getType() != Material.DRAGON_EGG) {
            return;
        }
        BlockVector delta = event.getToBlock().getLocation().subtract(event.getBlock().getLocation()).toVector().toBlockVector();
        api.moveBlock(event.getBlock(), delta);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onLeavesDecay(LeavesDecayEvent event) {
        api.freeBlock(event.getBlock());
    }


}
