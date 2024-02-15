package dev.thorinwasher.blockutil.listener;

import dev.thorinwasher.blockutil.BlockUtil;
import dev.thorinwasher.blockutil.util.BlockHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.util.BlockVector;

public class BlockEventListener implements Listener {

    private final BlockUtil api;

    public BlockEventListener(BlockUtil api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onBlockDropItem(BlockDropItemEvent event) {
        if (api.blockCanNotDropItems(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockBreak(BlockBreakEvent event) {
        api.enableItemDrops(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockBurn(BlockBurnEvent event) {
        api.enableItemDrops(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockFade(BlockFadeEvent event) {
        api.enableItemDrops(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockForm(BlockFormEvent event) {
        api.enableItemDrops(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onBlockFromTo(BlockFromToEvent event) {
        if (event.getBlock().getType() == Material.DRAGON_EGG) {
            BlockVector delta = event.getToBlock().getLocation().subtract(event.getBlock().getLocation()).toVector().toBlockVector();
            api.moveBlock(event.getBlock(), delta);
            return;
        }
        if (api.blockCanNotDropItems(event.getToBlock())) {
            event.setCancelled(true);
            BlockHelper.breakBlock(event.getToBlock(), api);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onLeavesDecay(LeavesDecayEvent event) {
        api.enableItemDrops(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onBlockPhysicsEvent(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (block.getType().isAir()) {
            return;
        }
        if (!block.getBlockData().isSupported(block) && api.blockCanNotDropItems(block)) {
            event.setCancelled(true);
            BlockHelper.breakBlock(block, api);
        }
    }
}
