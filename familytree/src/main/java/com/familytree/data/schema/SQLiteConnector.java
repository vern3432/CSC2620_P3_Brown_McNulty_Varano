package com.familytree.data.schema;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SQLiteConnector {
    private static final String url = "jdbc:sqlite:finnFamilyTree.db";

    
    /** 
     * @return Connection
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createSchema(Connection connection) throws IOException, SQLException {
        try (InputStream inputStream = SQLiteConnector.class.getResourceAsStream("/FamilyTreeSchema.sql")) {
            if (inputStream != null) {
                // Read SQL script from the input stream
                Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                String sqlScript = scanner.hasNext() ? scanner.next() : "";

                // Create statement
                try (Statement statement = connection.createStatement()) {
                    statement.execute("PRAGMA foreign_keys = ON;"); // Enabling foreign key constraint checking
                    // Execute SQL script
                    statement.executeUpdate(sqlScript);
                    System.out.println("Schema created successfully.");
                }
            } else {
                System.out.println("Schema file not found.");
            }
        }
    }
}
