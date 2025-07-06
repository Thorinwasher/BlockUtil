package dev.thorinwasher.blockutil;

import dev.thorinwasher.blockutil.api.BlockPosition;
import dev.thorinwasher.blockutil.api.BlockUtilAPI;
import dev.thorinwasher.blockutil.api.event.BlockDisableDropEvent;
import dev.thorinwasher.blockutil.database.DatabaseInterface;
import dev.thorinwasher.blockutil.listener.*;
import dev.thorinwasher.blockutil.util.BlockHelper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

@ApiStatus.Internal
public class BlockUtil implements BlockUtilAPI {

    private final DatabaseInterface databaseInterface;
    private final Map<UUID, Set<BlockPosition>> trackedBlocks = new HashMap<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Consumer<BlockDisableDropEvent> dropEventHandler;

    public BlockUtil(DatabaseInterface databaseInterface, Consumer<BlockDisableDropEvent> dropEventHandler) {
        this.databaseInterface = databaseInterface;
        this.dropEventHandler = dropEventHandler;
    }

    public void registerListeners(Plugin plugin) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BlockEventListener(this), plugin);
        pluginManager.registerEvents(new PistonEventListener(this), plugin);
        pluginManager.registerEvents(new ExplodeEventListener(this), plugin);
        pluginManager.registerEvents(new EntityEventListener(this), plugin);
        pluginManager.registerEvents(new BlockGrowEventListener(this), plugin);
        pluginManager.registerEvents(new WorldEventListener(this), plugin);
    }

    @Override
    public void disableItemDrops(Block block) {
        List<Block> blockStructure = BlockHelper.getBlockStructure(block);
        UUID worldUuid = block.getWorld().getUID();
        blockStructure.forEach(blockInStructure -> {
            BlockPosition blockLocation = BlockPosition.from(blockInStructure.getLocation());
            blockItemDropsDisabled(blockLocation, worldUuid)
                    .thenAcceptAsync((itemDropsDisabled) -> {
                        if (!itemDropsDisabled) {
                            return;
                        }
                        trackedBlocks.get(worldUuid).add(blockLocation);
                        databaseInterface.trackBlock(blockLocation, worldUuid);
                    }, executor);
        });
    }

    @Override
    public void enableItemDrops(Block block) {
        List<Block> blockStructure = BlockHelper.getBlockStructure(block);
        UUID worldUuid = block.getWorld().getUID();
        blockStructure.forEach(blockInStructure -> {
            BlockPosition blockLocation = BlockPosition.from(blockInStructure.getLocation());
            blockItemDropsDisabled(blockLocation, worldUuid)
                    .thenAcceptAsync(itemDropsDisabled -> {
                        if (!itemDropsDisabled) {
                            return;
                        }
                        databaseInterface.freeBlock(blockLocation, worldUuid);
                        trackedBlocks.get(worldUuid).remove(blockLocation);
                    }, executor);
        });

    }

    @Override
    public CompletableFuture<Boolean> blockItemDropsDisabled(BlockPosition block, UUID worldUuid) {
        return CompletableFuture.supplyAsync(() -> {
            Set<BlockPosition> positions = trackedBlocks.get(worldUuid);
            return positions != null && positions.contains(block);
        }, executor);
    }

    @Override
    public void moveBlock(Block from, BlockVector delta) {
        BlockPosition pos = BlockPosition.from(from.getLocation());
        UUID worldUuid = from.getWorld().getUID();
        blockItemDropsDisabled(pos, worldUuid)
                .thenAcceptAsync(itemDropsDisable -> {
                    if (!itemDropsDisable) {
                        return;
                    }
                    databaseInterface.moveBlock(pos, delta, worldUuid);
                    Set<BlockPosition> tracked = trackedBlocks.get(worldUuid);
                    tracked.remove(pos);
                    tracked.add(pos.add(delta));
                }, executor);
    }

    public void onWorldLoad(UUID worldUuid) {
        databaseInterface.getAllBlocks(worldUuid)
                .thenAcceptAsync(tracked -> trackedBlocks.put(worldUuid, tracked), executor);
    }

    public void onWorldUnload(UUID worldUuid) {
        executor.execute(() -> {
            trackedBlocks.remove(worldUuid);
        });
    }

    public void loadWorlds() {
        executor.execute(() -> Bukkit.getWorlds().stream()
                .map(World::getUID)
                .forEach(worldUuid -> trackedBlocks.put(worldUuid, databaseInterface.getAllBlocks(worldUuid).join())));
    }

    public BlockDisableDropEvent newDisable(Block block) {
        BlockDisableDropEvent output = new BlockDisableDropEvent(block);
        dropEventHandler.accept(output);
        return output;
    }
}
