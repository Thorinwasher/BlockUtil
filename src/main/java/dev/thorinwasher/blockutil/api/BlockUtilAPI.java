package dev.thorinwasher.blockutil.api;

import com.google.common.base.Preconditions;
import dev.thorinwasher.blockutil.BlockUtil;
import dev.thorinwasher.blockutil.api.event.BlockDisableDropEvent;
import dev.thorinwasher.blockutil.database.DatabaseInterface;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;

import java.sql.Connection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface BlockUtilAPI {


    /**
     * Track this block, make it so that no items can be generated from breaking this block.
     *
     * @param block <p>The block to track</p>
     */
    void disableItemDrops(Block block);

    /**
     * Free this block, make it so that items can be generated from breaking this block.
     *
     * @param block <p>The block to free</p>
     */
    void enableItemDrops(Block block);

    /**
     * Check whether the block can drop items, i.e. if it has been tracked
     *
     * @param block <p>The block to check</p>
     * @return <p>True if the block is tracked</p>
     */
    default boolean blockItemDropsDisabled(Block block) {
        return blockItemDropsDisabled(BlockPosition.from(block.getLocation()), block.getWorld().getUID()).join();
    }

    /**
     * Check whether the block can drop items, i.e. if it has been tracked
     *
     * @param block     The block position to check
     * @param worldUuid The world uuid of the block world
     * @return A completable future with the value true if the block is tracked
     */
    CompletableFuture<Boolean> blockItemDropsDisabled(BlockPosition block, UUID worldUuid);

    /**
     * Move all tracking of a block to a new location
     *
     * @param from  <p>Original location</p>
     * @param delta <p>The difference the new location and original location</p>
     */
    void moveBlock(Block from, BlockVector delta);

    class Builder {


        private Supplier<Connection> connectionSupplier;
        private String prefix = "";
        private Consumer<BlockDisableDropEvent> dropEventHandler = itemStack -> {
        };
        private Plugin owner;

        public Builder withConnectionSupplier(Supplier<Connection> connectionSupplier) {
            this.connectionSupplier = connectionSupplier;
            return this;
        }

        public Builder withDbPrefix(String prefix) {
            Preconditions.checkNotNull(prefix);
            this.prefix = prefix;
            return this;
        }

        public Builder withDropEventHandler(Consumer<BlockDisableDropEvent> dropEventHandler) {
            Preconditions.checkNotNull(dropEventHandler);
            this.dropEventHandler = dropEventHandler;
            return this;
        }

        public Builder withPluginOwner(Plugin owner) {
            this.owner = owner;
            return this;
        }

        public BlockUtilAPI build() {
            Preconditions.checkNotNull(connectionSupplier);
            DatabaseInterface databaseInterface = new DatabaseInterface(connectionSupplier, prefix);
            databaseInterface.init();
            BlockUtil built = new BlockUtil(databaseInterface, dropEventHandler);
            built.registerListeners(owner);
            built.loadWorlds();
            return built;
        }
    }


}
