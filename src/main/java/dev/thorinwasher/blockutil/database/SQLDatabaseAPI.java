package dev.thorinwasher.blockutil.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLDatabaseAPI {

    Connection getConnection() throws SQLException;
}
