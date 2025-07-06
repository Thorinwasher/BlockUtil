package dev.thorinwasher.blockutil.listener;

import dev.thorinwasher.blockutil.BlockUtil;
import dev.thorinwasher.blockutil.api.event.BlockDisableDropEvent;
import dev.thorinwasher.blockutil.util.BlockHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.util.BlockVector;

import java.util.List;

public class BlockEventListener implements Listener {

    private final BlockUtil api;

    public BlockEventListener(BlockUtil api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onBlockDropItem(BlockDropItemEvent event) {
        if (!api.blockItemDropsDisabled(event.getBlock())) {
            return;
        }
        BlockDisableDropEvent blockDisableDropEvent = api.newDisable(event.getBlock());
        if (blockDisableDropEvent.getDisableDrops()) {
            List<Item> items = event.getItems();
            items.clear();
            Location location = event.getBlock().getLocation().toCenterLocation();
            blockDisableDropEvent.getDropOverride()
                    .stream()
                    .map(itemStack -> {
                        Item item = event.getBlock().getWorld().createEntity(location, Item.class);
                        item.setItemStack(itemStack);
                        return item;
                    })
                    .forEach(items::add);
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onBlockFromTo(BlockFromToEvent event) {
        if (event.getBlock().getType() == Material.DRAGON_EGG) {
            BlockVector delta = event.getToBlock().getLocation().subtract(event.getBlock().getLocation()).toVector().toBlockVector();
            api.moveBlock(event.getBlock(), delta);
            return;
        }
        if (api.blockItemDropsDisabled(event.getToBlock())) {
            event.setCancelled(true);
            BlockHelper.breakBlock(event.getToBlock(), api);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onLeavesDecay(LeavesDecayEvent event) {
        if (api.blockItemDropsDisabled(event.getBlock())) {
            event.setCancelled(true);
            BlockHelper.breakBlock(event.getBlock(), api);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onBlockPhysicsEvent(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (block.getType().isAir()) {
            return;
        }
        if (block.getBlockData().isSupported(block) || !api.blockItemDropsDisabled(block)) {
            return;
        }
        BlockDisableDropEvent disableDropEvent = api.newDisable(block);
        if (!disableDropEvent.getDisableDrops()) {
            return;
        }
        event.setCancelled(true);
        World world = block.getWorld();
        Location location = block.getLocation().toCenterLocation();
        disableDropEvent.getDropOverride().stream()
                .forEach(itemStack -> world.dropItemNaturally(location, itemStack));
        BlockHelper.breakBlock(block, api);
    }
}
