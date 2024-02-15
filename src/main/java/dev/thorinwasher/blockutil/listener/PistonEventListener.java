package dev.thorinwasher.blockutil.listener;

import dev.thorinwasher.blockutil.BlockUtil;
import dev.thorinwasher.blockutil.util.BlockHelper;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.List;

public class PistonEventListener implements Listener {

    private final BlockUtil api;

    public PistonEventListener(BlockUtil api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onBlockPistonExtend(BlockPistonExtendEvent event) {
        handlePistonEvent(event.getBlocks(), event.getDirection());
        if (api.blockCanNotDropItems(event.getBlock())) {
            api.disableItemDrops(event.getBlock().getRelative(event.getDirection()));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onBlockPistonRetract(BlockPistonRetractEvent event) {
        handlePistonEvent(event.getBlocks(), event.getDirection());
        if (api.blockCanNotDropItems(event.getBlock()) && event.getBlocks().isEmpty()) {
            api.enableItemDrops(event.getBlock().getRelative(event.getDirection().getOppositeFace()));
        }
    }

    private void handlePistonEvent(List<Block> blocks, BlockFace direction) {
        for (Block block : blocks) {
            if (willBreakOnPistonMove(blocks, block, direction)) {
                BlockHelper.breakBlockIfTracked(block, api);
            } else {
                api.moveBlock(block, direction.getDirection().toBlockVector());
            }
        }
    }

    private boolean willBreakOnPistonMove(List<Block> blocks, Block block, BlockFace direction) {
        if (block.getPistonMoveReaction() == PistonMoveReaction.BREAK) {
            return true;
        }
        Block blockDown = block.getRelative(BlockFace.DOWN);
        if (blocks.contains(blockDown)) {
            return willBreakOnPistonMove(blocks, blockDown, direction);
        }
        return !block.getBlockData().isSupported(block.getRelative(direction));
    }
}
