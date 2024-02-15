package dev.thorinwasher.blockutil.listener;

import dev.thorinwasher.blockutil.BlockUtil;
import dev.thorinwasher.blockutil.util.BlockHelper;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.logging.Level;
import java.util.stream.Stream;

public class EntityEventListener implements Listener {

    private final BlockUtil blockUtilAPI;
    private static final String NO_BLOCK_DROP = "noBlockDrop";

    public EntityEventListener(BlockUtil blockUtilAPI) {
        this.blockUtilAPI = blockUtilAPI;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onMonsterSpawn(CreatureSpawnEvent event) {
        Block baseBlock = event.getLocation().getBlock();
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN && checkSnowman(baseBlock)) {
            event.setCancelled(true);
        }
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM && checkIrongolem(baseBlock)) {
            event.setCancelled(true);
        }
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BUILD_WITHER && checkWither(baseBlock)) {
            event.setCancelled(true);
        }
    }

    private boolean checkWither(Block baseBlock) {
        Stream<Block> blockStream = Stream.of(
                baseBlock,
                baseBlock.getRelative(0, 1, 0),
                baseBlock.getRelative(1, 1, 0),
                baseBlock.getRelative(-1, 1, 0),
                baseBlock.getRelative(0, 1, 1),
                baseBlock.getRelative(0, 1, -1),
                baseBlock.getRelative(0, 2, 0),
                baseBlock.getRelative(1, 2, 0),
                baseBlock.getRelative(-1, 2, 0),
                baseBlock.getRelative(0, 2, 1),
                baseBlock.getRelative(0, 2, -1)
        );
        return blockStream.anyMatch(blockUtilAPI::blockIsTracked);
    }

    private boolean checkIrongolem(Block baseBlock) {
        Stream<Block> blockStream = Stream.of(baseBlock,
                baseBlock.getRelative(0, 1, 0),
                baseBlock.getRelative(1, 1, 0),
                baseBlock.getRelative(-1, 1, 0),
                baseBlock.getRelative(0, 1, 1),
                baseBlock.getRelative(0, 1, -1),
                baseBlock.getRelative(0, 2, 0));
        return blockStream.anyMatch(blockUtilAPI::blockIsTracked);
    }

    private boolean checkSnowman(Block baseBlock) {
        Stream<Block> blockStream = Stream.of(baseBlock,
                baseBlock.getRelative(0, 1, 0),
                baseBlock.getRelative(0, 2, 0));
        return blockStream.anyMatch(blockUtilAPI::blockIsTracked);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    private void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock fallingBlock) {
            if (event.getBlock().getType().isAir()) {
                if (fallingBlock.getMetadata(NO_BLOCK_DROP).get(0).asBoolean()) {
                    blockUtilAPI.trackBlock(event.getBlock());
                }
            } else if (blockUtilAPI.blockIsTracked(event.getBlock())) {
                event.getEntity().setMetadata(NO_BLOCK_DROP, new FixedMetadataValue(blockUtilAPI, true));
                fallingBlock.setDropItem(false);
                blockUtilAPI.freeBlock(event.getBlock());
            }
            return;
        }
        if(event.getEntity() instanceof Wither){
            BlockHelper.breakBlock(event.getBlock(), blockUtilAPI);
        }
        if(event.getBlock().getType() == Material.FARMLAND){
            Block up = event.getBlock().getRelative(BlockFace.UP);
            if(blockUtilAPI.blockIsTracked(up)){
                event.setCancelled(true);
                BlockHelper.breakBlock(up, blockUtilAPI);
                event.getBlock().setBlockData(event.getBlockData());
            }
        }
    }
}
