package com.familytree;

import com.familytree.data.schema.SQLiteConnector;

import javax.swing.SwingUtilities;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = SQLiteConnector.connect()) {
            // Setup the schema
            SQLiteConnector.createSchema(conn);
            SwingUtilities.invokeLater(() -> new FamilyTreeGUI(conn));
        } catch (Exception ex) {
            System.err.println("Failed to start application");
        }
    }
}