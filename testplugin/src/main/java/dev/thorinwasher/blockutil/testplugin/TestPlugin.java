package dev.thorinwasher.blockutil.testplugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.thorinwasher.blockutil.api.BlockUtilAPI;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class TestPlugin extends JavaPlugin {

    private BlockUtilAPI api;

    @Override
    public void onEnable() {
        try {
            HikariConfig config = getHikariConfigForSqlite(new File(this.getDataFolder(), "database.db"));
            HikariDataSource source = new HikariDataSource(config);
            this.api = new BlockUtilAPI.Builder()
                    .withPluginOwner(this)
                    .withConnectionSupplier(() -> {
                        try {
                            return source.getConnection();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commandsHandler -> {
            commandsHandler.registrar().register(
                    Commands.literal("blockutil")
                            .then(Commands.literal("mark")
                                    .executes(context -> {
                                        Player sender = (Player) context.getSource().getSender();
                                        Block block = sender.rayTraceBlocks(32).getHitBlock();
                                        api.disableItemDrops(block);
                                        return 1;
                                    })
                            ).then(Commands.literal("status")
                                    .executes(context -> {
                                        Player sender = (Player) context.getSource().getSender();
                                        Block block = sender.rayTraceBlocks(32).getHitBlock();
                                        if (api.blockItemDropsDisabled(block)) {
                                            sender.sendMessage("drops disabled");
                                        } else {
                                            sender.sendMessage("Nothing tracked");
                                        }
                                        return 1;
                                    })
                            ).build()
            );
        });
    }

    private static @NotNull HikariConfig getHikariConfigForSqlite(File dataFolder) throws IOException {
        File databaseFile = new File(dataFolder, "garden.db");
        if (!databaseFile.exists() && !databaseFile.getParentFile().mkdirs() && !databaseFile.createNewFile()) {
            throw new IOException("Could not create file or dirs");
        }
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("SQLiteConnectionPool");
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + databaseFile);
        return hikariConfig;
    }
}
