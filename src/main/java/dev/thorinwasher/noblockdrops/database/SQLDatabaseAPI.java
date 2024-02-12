package dev.thorinwasher.noblockdrops.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLDatabaseAPI {

    Connection getConnection() throws SQLException;
}
