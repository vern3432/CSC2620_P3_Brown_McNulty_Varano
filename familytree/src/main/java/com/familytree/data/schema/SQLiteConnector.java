package com.familytree.data.schema;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SQLiteConnector {

    public static Connection connect() {
        String url = "jdbc:sqlite:finnFamilyTree.db";
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

                System.out.println(sqlScript);
                // Create statement
                try (Statement statement = connection.createStatement()) {

                    // Execute SQL script
                    statement.execute(sqlScript);
                    System.out.println("Schema created successfully.");
                }
            } else {
                System.out.println("Schema file not found.");
            }
        }
    }
}
