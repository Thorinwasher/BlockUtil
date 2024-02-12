package dev.thorinwasher.noblockdrops.listener;

import dev.thorinwasher.noblockdrops.api.NoBlockDropsAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplodeEventListener implements Listener {

    private final NoBlockDropsAPI plugin;

    public ExplodeEventListener(NoBlockDropsAPI plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().forEach(plugin::freeBlock);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().forEach(plugin::freeBlock);
    }
}
