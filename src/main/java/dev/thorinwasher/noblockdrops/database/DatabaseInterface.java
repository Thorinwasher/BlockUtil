package dev.thorinwasher.noblockdrops.database;

import dev.thorinwasher.noblockdrops.BlockLocation;
import dev.thorinwasher.noblockdrops.NoBlockDrops;
import dev.thorinwasher.noblockdrops.util.FileUtil;
import org.bukkit.util.BlockVector;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DatabaseInterface {

    private final SQLDatabaseAPI databaseAPI;
    private Map<SQLQuery, String> queries = loadQueries();

    public DatabaseInterface(SQLDatabaseAPI databaseAPI) {
        this.databaseAPI = databaseAPI;
    }

    public void trackBlock(BlockLocation block) {
        try (Connection connection = databaseAPI.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(queries.get(SQLQuery.TRACK_BLOCK))) {
                insertBlockParameters(preparedStatement, block);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeBlock(BlockLocation block) {
        try (Connection connection = databaseAPI.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(queries.get(SQLQuery.FREE_BLOCK))) {
                insertBlockParameters(preparedStatement, block);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void moveBlock(BlockLocation from, BlockVector delta) {
        try (Connection connection = databaseAPI.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(queries.get(SQLQuery.FREE_BLOCK))) {
                insertBlockParameters(preparedStatement, from);
                preparedStatement.execute();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(queries.get(SQLQuery.TRACK_BLOCK))) {
                insertBlockParameters(preparedStatement, from.add(delta));
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertBlockParameters(PreparedStatement preparedStatement, BlockLocation block) throws SQLException {
        preparedStatement.setInt(1, block.getX());
        preparedStatement.setInt(2, block.getY());
        preparedStatement.setInt(3, block.getZ());
        preparedStatement.setString(4, block.getWorld().toString());
    }

    public void init() {
        try (Connection connection = databaseAPI.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(queries.get(SQLQuery.CREATE_BLOCK_TABLE))) {
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<BlockLocation> getAllBlocks() {
        List<BlockLocation> output = new ArrayList<>();
        try (Connection connection = databaseAPI.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(queries.get(SQLQuery.SELECT_ALL_BLOCKS))) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int x = resultSet.getInt("x");
                    int y = resultSet.getInt("y");
                    int z = resultSet.getInt("z");
                    UUID world = UUID.fromString(resultSet.getString("world"));
                    output.add(new BlockLocation(x, y, z, world));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return output;
    }

    private Map<SQLQuery, String> loadQueries() {
        Map<SQLQuery, String> output = new EnumMap<>(SQLQuery.class);
        for (SQLQuery query : SQLQuery.values()) {
            try (InputStream inputStream = NoBlockDrops.class.getResourceAsStream("/database/" + query.getFileName() + ".sql")) {
                output.put(query, FileUtil.readStreamToString(inputStream));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return output;
    }
}
