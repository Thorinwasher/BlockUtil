package dev.thorinwasher.noblockdrops;

import dev.thorinwasher.noblockdrops.api.NoBlockDropsAPI;
import dev.thorinwasher.noblockdrops.database.DatabaseInterface;
import dev.thorinwasher.noblockdrops.database.SQLDatabaseAPI;
import dev.thorinwasher.noblockdrops.database.SQLiteDatabase;
import dev.thorinwasher.noblockdrops.listener.BlockEventListener;
import dev.thorinwasher.noblockdrops.listener.ExplodeEventListener;
import dev.thorinwasher.noblockdrops.listener.PistonEventListener;
import dev.thorinwasher.noblockdrops.thread.ThreadQueue;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class NoBlockDrops extends JavaPlugin implements NoBlockDropsAPI {

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
        this.getServer().getServicesManager().register(NoBlockDropsAPI.class, this, this, ServicePriority.High);
        registerListeners();
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BlockEventListener(this), this);
        pluginManager.registerEvents(new PistonEventListener(this), this);
        pluginManager.registerEvents(new ExplodeEventListener(this), this);
    }

    @Override
    public void onDisable() {
        ThreadQueue.disableQueue();
        trackedBlocks.clear();
    }

    @Override
    public void trackBlock(Block block) {
        if (blockIsTracked(block)) {
            return;
        }
        BlockLocation blockLocation = new BlockLocation(block.getLocation());
        ThreadQueue.addToQueue(() -> databaseInterface.trackBlock(blockLocation));
        trackedBlocks.add(blockLocation);
    }

    @Override
    public void freeBlock(Block block) {
        if (!blockIsTracked(block)) {
            return;
        }
        BlockLocation blockLocation = new BlockLocation(block.getLocation());
        ThreadQueue.addToQueue(() -> databaseInterface.freeBlock(blockLocation));
        trackedBlocks.remove(blockLocation);
    }

    @Override
    public boolean blockIsTracked(Block block) {
        return trackedBlocks.contains(new BlockLocation(block.getLocation()));
    }

    @Override
    public void moveBlock(Block from, BlockVector delta) {
        if (!blockIsTracked(from)) {
            return;
        }
        BlockLocation blockLocation = new BlockLocation(from.getLocation());
        ThreadQueue.addToQueue(() -> databaseInterface.moveBlock(blockLocation, delta));
        trackedBlocks.remove(blockLocation);
        trackedBlocks.add(blockLocation.add(delta));
    }
}
