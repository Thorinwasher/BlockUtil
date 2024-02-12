package dev.thorinwasher.blockutil.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents an SQLite database
 */
public class SQLiteDatabase implements SQLDatabaseAPI {

    private HikariDataSource source;

    /**
     * Instantiates a new SQL database
     *
     * @param databaseFile <p>The database file to load</p>
     */
    public SQLiteDatabase(File databaseFile) {
        setupSQLITE(databaseFile);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    /**
     * Sets up SQLite
     *
     * @param databaseFile <p>
     *                     The database file to load
     *                     </p>
     */
    private void setupSQLITE(File databaseFile) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile);
        config.setMaxLifetime(60000); // 60 Sec
        config.setIdleTimeout(45000); // 45 Sec
        config.setMaximumPoolSize(50); // 50 Connections (including idle connections)
        source = new HikariDataSource(config);
    }

}
