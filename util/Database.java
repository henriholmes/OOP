package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



    public class Database {
    private static final String URL = "jdbc:sqlite:studenten.db";

    public static Connection connect() throws SQLException {
        try {
            // ⬇️ SQLite JDBC-Treiber explizit laden
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite-Treiber nicht gefunden", e);
        }

        return DriverManager.getConnection(URL);
    }
}