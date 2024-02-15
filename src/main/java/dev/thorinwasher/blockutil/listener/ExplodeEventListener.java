package dev.thorinwasher.blockutil.listener;

import dev.thorinwasher.blockutil.api.BlockUtilAPI;
import dev.thorinwasher.blockutil.thread.ThreadHelper;
import dev.thorinwasher.blockutil.util.BlockHelper;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplodeEventListener implements Listener {

    private final BlockUtilAPI plugin;

    public ExplodeEventListener(BlockUtilAPI plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().forEach(block -> BlockHelper.breakBlockIfTracked(block, plugin));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().forEach(block -> BlockHelper.breakBlockIfTracked(block, plugin));
    }
}
