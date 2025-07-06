package dev.thorinwasher.blockutil.database;

import dev.thorinwasher.blockutil.api.BlockPosition;
import dev.thorinwasher.blockutil.util.EncoderDecoder;
import dev.thorinwasher.blockutil.util.FileUtil;
import org.bukkit.util.BlockVector;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public class DatabaseInterface {

    private final Supplier<Connection> connectionSupplier;
    private final Map<SQLQuery, String> queries;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public DatabaseInterface(Supplier<Connection> connectionSupplier, String prefix) {
        queries = loadQueries(prefix);
        this.connectionSupplier = connectionSupplier;
    }

    public CompletableFuture<Void> trackBlock(BlockPosition block, UUID worldUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = connectionSupplier.get()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(queries.get(SQLQuery.TRACK_BLOCK))) {
                    insertBlockParameters(preparedStatement, block, worldUuid);
                    preparedStatement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }, executor);
    }

    public CompletableFuture<Void> freeBlock(BlockPosition block, UUID worldUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = connectionSupplier.get()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(queries.get(SQLQuery.FREE_BLOCK))) {
                    insertBlockParameters(preparedStatement, block, worldUuid);
                    preparedStatement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }, executor);
    }

    public CompletableFuture<Void> moveBlock(BlockPosition from, BlockVector delta, UUID worldUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = connectionSupplier.get()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(queries.get(SQLQuery.FREE_BLOCK))) {
                    insertBlockParameters(preparedStatement, from, worldUuid);
                    preparedStatement.execute();
                }
                try (PreparedStatement preparedStatement = connection.prepareStatement(queries.get(SQLQuery.TRACK_BLOCK))) {
                    insertBlockParameters(preparedStatement, from.add(delta), worldUuid);
                    preparedStatement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }, executor);
    }

    private void insertBlockParameters(PreparedStatement preparedStatement, BlockPosition block, UUID worldUuid) throws SQLException {
        preparedStatement.setInt(1, block.x());
        preparedStatement.setInt(2, block.y());
        preparedStatement.setInt(3, block.z());
        preparedStatement.setBytes(4, EncoderDecoder.asBytes(worldUuid));
    }

    public void init() {
        try (Connection connection = connectionSupplier.get()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(queries.get(SQLQuery.CREATE_BLOCK_TABLE))) {
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Set<BlockPosition>> getAllBlocks(UUID worldUuid) {
        return CompletableFuture.supplyAsync(() -> {
            Set<BlockPosition> output = new HashSet<>();
            try (Connection connection = connectionSupplier.get()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(queries.get(SQLQuery.SELECT_ALL_BLOCKS))) {
                    preparedStatement.setBytes(1, EncoderDecoder.asBytes(worldUuid));
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        int x = resultSet.getInt("x");
                        int y = resultSet.getInt("y");
                        int z = resultSet.getInt("z");
                        output.add(new BlockPosition(x, y, z));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return output;
        }, executor);
    }

    private static Map<SQLQuery, String> loadQueries(String prefix) {
        Map<SQLQuery, String> output = new EnumMap<>(SQLQuery.class);
        for (SQLQuery query : SQLQuery.values()) {
            try (InputStream inputStream = DatabaseInterface.class.getResourceAsStream("/database/" + query.getFileName() + ".sql")) {
                output.put(query, FileUtil.readStreamToString(inputStream).replace("@blockTable@", prefix + "blockTable"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return output;
    }
}
