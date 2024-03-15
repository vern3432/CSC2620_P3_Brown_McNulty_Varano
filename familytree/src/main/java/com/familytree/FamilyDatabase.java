package com.familytree;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter; 
import java.io.FileReader; 
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*; 
public class FamilyDatabase {
    private Connection connection;
    private static final String DB_FILE_NAME = "FamilyTree.db";

    public FamilyDatabase(String dbFilePath) throws ClassNotFoundException, IOException {

        try {
        //  String url = "jdbc:sqlite:" + new File(DB_FILE_PATH).getAbsolutePath();
             Class.forName("org.sqlite.JDBC");
             System.out.println("Loading Input Stream");
             String basePath = System.getProperty("user.dir");

             // Construct the absolute path of the database file
              dbFilePath = basePath + File.separator + DB_FILE_NAME;
 
             // Load the JDBC driver
             Class.forName("org.sqlite.JDBC");
             String[] types = {"TABLE"};

             connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
             DatabaseMetaData metaData = connection.getMetaData();

             ResultSet tables = metaData.getTables(null, null, "%", types);
             while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println(tableName);
            }

            System.out.println("Loaded Connection");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        //For potential new family feature 
    private void createTables() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Clients (" +
                    "client_id INTEGER PRIMARY KEY," +
                    "client_name TEXT)");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS FamilyMembers (" +
                    "member_id INTEGER PRIMARY KEY," +
                    "name TEXT," +
                    "birth_date DATE," +
                    "death_date DATE," +
                    "is_deceased BOOLEAN," +
                    "current_residence TEXT," +
                    "client_id INTEGER," +
                    "FOREIGN KEY(client_id) REFERENCES Clients(client_id))");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Relationships (" +
                    "relationship_id INTEGER PRIMARY KEY," +
                    "member_id INTEGER," +
                    "related_member_id INTEGER," +
                    "relation_type TEXT," +
                    "FOREIGN KEY(member_id) REFERENCES FamilyMembers(member_id)," +
                    "FOREIGN KEY(related_member_id) REFERENCES FamilyMembers(member_id))");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Addresses (" +
                    "address_id INTEGER PRIMARY KEY," +
                    "city TEXT," +
                    "member_id INTEGER," +
                    "FOREIGN KEY(member_id) REFERENCES FamilyMembers(member_id))");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Events (" +
                    "event_id INTEGER PRIMARY KEY," +
                    "event_date DATE," +
                    "event_type TEXT," +
                    "member_id INTEGER," +
                    "FOREIGN KEY(member_id) REFERENCES FamilyMembers(member_id))");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertClient(int clientId, String clientName) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Clients(client_id, client_name) VALUES (?, ?)");
            statement.setInt(1, clientId);
            statement.setString(2, clientName);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Method to get data from Clients table
    public String getClientName(int clientId) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT client_name FROM Clients WHERE client_id = ?");
            statement.setInt(1, clientId);
            ResultSet resultSet = statement.executeQuery();
            String clientName = resultSet.getString("client_name");
            resultSet.close();
            statement.close();
            return clientName;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //  (FamilyMembers, Relationships, Addresses, Events)

    // Method to export data from a table to a CSV file
    public void exportToCSV(String tableName, String csvFilePath) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Write column names to CSV file
            StringBuilder header = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                header.append(metaData.getColumnName(i));
                if (i < columnCount) header.append(",");
            }
            header.append("\n");

            StringBuilder data = new StringBuilder();
            data.append(header);

            // Write data to CSV file
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    data.append(resultSet.getString(i));
                    if (i < columnCount) data.append(",");
                }
                data.append("\n");
            }

            // Write data to file
            FileWriter writer = new FileWriter(csvFilePath);
            writer.write(data.toString());
            writer.close();

            resultSet.close();
            statement.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // Method to import data from a CSV file into a table
    public void importFromCSV(String tableName, String csvFilePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // Assuming the order of columns in CSV matches with table schema
                switch (tableName) {
                    case "Clients":
                        insertClient(Integer.parseInt(data[0]), data[1]);
                        break;


                    }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        FamilyDatabase familyDatabase = new FamilyDatabase("FamilyTree.db");
        System.out.println("Main CLass formed");

        // Example usage
        familyDatabase.insertClient(1, "John Doe");
        familyDatabase.exportToCSV("Clients", "clients.csv");
        familyDatabase.importFromCSV("Clients", "clients.csv");
        System.out.println(familyDatabase.getClientName(1)); //  "John Doe"
    }
}
