package com.familytree;

import com.familytree.data.schema.SQLiteConnector;
import com.familytree.parsing.TextFileParser;
import com.familytree.views.LoginDialog;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

public class Main {
    
    /** 
     * @param args
     */
    public static void main(String[] args) {
        try {
            final Connection conn = SQLiteConnector.connect();
            SQLiteConnector.createSchema(conn);
            loadInitialData(conn);
            SwingUtilities.invokeLater(() -> new LoginDialog(conn));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Failed to start application");
        }
    }

    private static void loadInitialData(Connection connection) throws IOException, SQLException, ParseException {
        try (InputStream inputStream = SQLiteConnector.class.getResourceAsStream("/data.txt")) {
            TextFileParser.parse(inputStream, connection);
        }
    }
}