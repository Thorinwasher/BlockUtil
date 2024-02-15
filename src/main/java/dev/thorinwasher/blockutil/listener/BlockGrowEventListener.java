package dev.thorinwasher.blockutil.listener;

import dev.thorinwasher.blockutil.BlockUtil;
import dev.thorinwasher.blockutil.util.BlockHelper;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class BlockGrowEventListener implements Listener {

    private final BlockUtil api;

    public BlockGrowEventListener(BlockUtil api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockGrow(BlockGrowEvent event) {
        Material blockType = event.getNewState().getType();
        if (blockType == Material.CACTUS || blockType == Material.SUGAR_CANE) {
            Block down = event.getBlock().getRelative(BlockFace.DOWN);
            if (api.blockIsTracked(down)) {
                api.trackBlock(event.getBlock());
            }
        }

        if ((blockType == Material.PUMPKIN || blockType == Material.MELON)
                && BlockHelper.getAdjacentBlocks(event.getBlock()).stream().filter(block -> Tag.CROPS.isTagged(block.getType())).anyMatch(api::blockIsTracked)) {
            api.trackBlock(event.getBlock());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onStructureGrow(StructureGrowEvent event) {
        if (api.blockIsTracked(event.getLocation().getBlock())) {
            event.getBlocks().stream().map(BlockState::getBlock).forEach(api::trackBlock);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockSpread(BlockSpreadEvent event) {
        if (api.blockIsTracked(event.getSource())) {
            api.trackBlock(event.getBlock());
        }
    }

}
