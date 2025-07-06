package dev.thorinwasher.blockutil.listener;

import dev.thorinwasher.blockutil.BlockUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldEventListener implements Listener {

    private final BlockUtil blockUtil;

    public WorldEventListener(BlockUtil blockUtil) {
        this.blockUtil = blockUtil;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        blockUtil.onWorldLoad(event.getWorld().getUID());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldUnLoad(WorldUnloadEvent event) {
        blockUtil.onWorldUnload(event.getWorld().getUID());
    }
}
