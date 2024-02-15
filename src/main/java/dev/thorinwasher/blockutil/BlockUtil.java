package dev.thorinwasher.blockutil;

import dev.thorinwasher.blockutil.api.BlockUtilAPI;
import dev.thorinwasher.blockutil.database.DatabaseInterface;
import dev.thorinwasher.blockutil.database.SQLDatabaseAPI;
import dev.thorinwasher.blockutil.database.SQLiteDatabase;
import dev.thorinwasher.blockutil.listener.BlockEventListener;
import dev.thorinwasher.blockutil.listener.BlockGrowEventListener;
import dev.thorinwasher.blockutil.listener.EntityEventListener;
import dev.thorinwasher.blockutil.listener.ExplodeEventListener;
import dev.thorinwasher.blockutil.listener.PistonEventListener;
import dev.thorinwasher.blockutil.thread.ThreadHelper;
import dev.thorinwasher.blockutil.thread.ThreadQueue;
import dev.thorinwasher.blockutil.util.BlockHelper;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockUtil extends JavaPlugin implements BlockUtilAPI {

    private DatabaseInterface databaseInterface;
    private Set<BlockLocation> trackedBlocks = new HashSet<>();

    @Override
    public void onEnable() {
        if (!this.getDataFolder().exists() && !this.getDataFolder().mkdirs()) {
            throw new RuntimeException("Unable to create plugin folder");
        }
        SQLDatabaseAPI sqlDatabaseAPI = new SQLiteDatabase(new File(this.getDataFolder(), "database.db"));
        this.databaseInterface = new DatabaseInterface(sqlDatabaseAPI);
        databaseInterface.init();
        trackedBlocks.addAll(databaseInterface.getAllBlocks());
        ThreadQueue.enableQueue(this);
        this.getServer().getServicesManager().register(BlockUtilAPI.class, this, this, ServicePriority.High);
        registerListeners();
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BlockEventListener(this), this);
        pluginManager.registerEvents(new PistonEventListener(this), this);
        pluginManager.registerEvents(new ExplodeEventListener(this), this);
        pluginManager.registerEvents(new EntityEventListener(this), this);
        pluginManager.registerEvents(new BlockGrowEventListener(this), this);
    }

    @Override
    public void onDisable() {
        ThreadQueue.disableQueue();
        trackedBlocks.clear();
    }

    @Override
    public void disableItemDrops(Block block) {
        List<Block> blockStructure = BlockHelper.getBlockStructure(block);
        blockStructure.forEach(blockInStructure -> {
            if (blockCanNotDropItems(block)) {
                return;
            }
            BlockLocation blockLocation = new BlockLocation(blockInStructure.getLocation());
            ThreadQueue.addToQueue(() -> databaseInterface.trackBlock(blockLocation));
            trackedBlocks.add(blockLocation);
        });
    }

    @Override
    public void enableItemDrops(Block block) {
        List<Block> blockStructure = BlockHelper.getBlockStructure(block);
        blockStructure.forEach(blockInStructure -> {
            if (!blockCanNotDropItems(block)) {
                return;
            }
            BlockLocation blockLocation = new BlockLocation(blockInStructure.getLocation());
            ThreadQueue.addToQueue(() -> databaseInterface.freeBlock(blockLocation));
            ThreadHelper.runGlobalTask(() -> trackedBlocks.remove(blockLocation), this);
        });

    }

    @Override
    public boolean blockCanNotDropItems(Block block) {
        return trackedBlocks.contains(new BlockLocation(block.getLocation()));
    }

    @Override
    public void moveBlock(Block from, BlockVector delta) {
        if (!blockCanNotDropItems(from)) {
            return;
        }
        BlockLocation blockLocation = new BlockLocation(from.getLocation());
        ThreadQueue.addToQueue(() -> databaseInterface.moveBlock(blockLocation, delta));
        trackedBlocks.remove(blockLocation);
        trackedBlocks.add(blockLocation.add(delta));
    }
}
