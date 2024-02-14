package dev.thorinwasher.blockutil.listener;

import dev.thorinwasher.blockutil.BlockUtil;
import org.bukkit.Material;
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
        if(api.blockIsTracked(event.getBlock())){
            api.trackBlock(event.getBlock().getRelative(event.getDirection()));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onBlockPistonRetract(BlockPistonRetractEvent event) {
        handlePistonEvent(event.getBlocks(), event.getDirection());
        if(api.blockIsTracked(event.getBlock()) && event.getBlocks().isEmpty()){
            api.freeBlock(event.getBlock().getRelative(event.getDirection().getOppositeFace()));
        }
    }

    private void handlePistonEvent(List<Block> blocks, BlockFace direction) {
        for (Block block : blocks) {
            if (block.getPistonMoveReaction() == PistonMoveReaction.BREAK) {
                if (api.blockIsTracked(block)) {
                    block.setType(Material.AIR);
                    api.freeBlock(block);
                }
            } else {
                api.moveBlock(block, direction.getDirection().toBlockVector());
            }
        }
    }
}
