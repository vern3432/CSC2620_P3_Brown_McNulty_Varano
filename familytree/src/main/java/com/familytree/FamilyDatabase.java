package com.familytree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FamilyDatabase {
    private static Connection connection;
    private static final String DB_FILE_NAME = "FamilyTree.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            String basePath = System.getProperty("user.dir");
            String dbFilePath = basePath + File.separator + DB_FILE_NAME;
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
            String[] types = {"TABLE"};

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
            DatabaseMetaData metaData = connection.getMetaData();

            ResultSet tables = metaData.getTables(null, null, "%", types);
            while (tables.next()) {
               String tableName = tables.getString("TABLE_NAME");
               System.out.println(tableName);
           }



        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createBlankTables() {
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
                    "client_id INTEGER REFERENCES Clients(client_id))");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Relationships (" +
                    "relationship_id INTEGER PRIMARY KEY," +
                    "member_id INTEGER REFERENCES FamilyMembers(member_id)," +
                    "related_member_id INTEGER REFERENCES FamilyMembers(member_id)," +
                    "relation_type TEXT)");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Addresses (" +
                    "address_id INTEGER PRIMARY KEY," +
                    "city TEXT," +
                    "member_id INTEGER REFERENCES FamilyMembers(member_id))");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Events (" +
                    "event_id INTEGER PRIMARY KEY," +
                    "event_date DATE," +
                    "event_type TEXT," +
                    "member_id INTEGER REFERENCES FamilyMembers(member_id))");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS EventAttendees (" +
                    "event_date DATE," +
                    "event_type TEXT," +
                    "event_id INTEGER REFERENCES Events(event_id)," +
                    "member_id INTEGER REFERENCES FamilyMembers(member_id))");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }public static int getNextClientId() {
        int nextClientId = 1; // Start from ID 1
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(client_id) AS max_id FROM Clients");
            if (resultSet.next()) {
                nextClientId = resultSet.getInt("max_id") + 1;
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextClientId;
    }




    public static List<FamilyMember> getAllFamilyMembers() {
        List<FamilyMember> familyMembers = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM FamilyMembers");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                Date birthDate = resultSet.getDate("birth_date");
                Date deathDate = resultSet.getDate("death_date");
                boolean isDeceased = resultSet.getBoolean("is_deceased");
                String currentResidence = resultSet.getString("current_residence");

                FamilyMember familyMember = new FamilyMember(name, birthDate, deathDate, isDeceased, currentResidence);
                familyMembers.add(familyMember);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return familyMembers;
    }

    public static List<Event> getAllEventsWithAttendees() {
        List<Event> events = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Events");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int eventId = resultSet.getInt("event_id");
                Date eventDate = resultSet.getDate("event_date");
                String eventType = resultSet.getString("event_type");
    
                Event event = new Event(eventId, eventDate, eventType);
    
                // Fetch attendees for the event
                List<FamilyMember> attendees = fetchAttendeesForEvent(eventId);
                for (FamilyMember attendee : attendees) {
                    event.addAttendee(attendee);
                }
    
                events.add(event);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    
    private static List<FamilyMember> fetchAttendeesForEvent(int eventId) {
        List<FamilyMember> attendees = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM EventAttendees WHERE event_id = ?");
            statement.setInt(1, eventId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int memberId = resultSet.getInt("member_id");
                // Fetch member details from FamilyMembers table based on memberId
                FamilyMember attendee = fetchFamilyMemberById(memberId);
                attendees.add(attendee);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendees;
    }
    
    private static FamilyMember fetchFamilyMemberById(int memberId) {
        FamilyMember familyMember = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM FamilyMembers WHERE member_id = ?");
            statement.setInt(1, memberId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                Date birthDate = resultSet.getDate("birth_date");
                Date deathDate = resultSet.getDate("death_date");
                boolean isDeceased = resultSet.getBoolean("is_deceased");
                String currentResidence = resultSet.getString("current_residence");
    
                familyMember = new FamilyMember(name, birthDate, deathDate, isDeceased, currentResidence);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return familyMember;
    }


    public static int addEvent(Date eventDate, String eventType, int memberId) {
        int eventId = getNextEventId();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Events (event_id, event_date, event_type, member_id) VALUES (?, ?, ?, ?)");
            statement.setInt(1, eventId);
            statement.setDate(2, eventDate != null ? new java.sql.Date(eventDate.getTime()) : null);
            statement.setString(3, eventType);
            statement.setInt(4, memberId);
            statement.executeUpdate();
            statement.close();
            System.out.println("Event inserted successfully for " + getMemberName(memberId) + ". Event ID: " + eventId);
            return eventId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean addAttendeeToEvent(int eventId, int memberId) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO EventAttendees (event_id, member_id) VALUES (?, ?)");
            statement.setInt(1, eventId);
            statement.setInt(2, memberId);
            statement.executeUpdate();
            statement.close();
            System.out.println("Attendee added successfully to event with ID: " + eventId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void addParsedDataToDatabase(List<String[]> parsedData) {
        for (String[] data : parsedData) {
            String dataType = data[0];
            String[] values = data[1].split(", ");

            switch (dataType) {
                case "FamilyMember":
                    insertFamilyMember(values);
                    break;
                case "Relationship":
                    insertRelationship(values);
                    break;
                case "Event":
                    insertEvent(values);
                    break;
                case "Address":
                    insertAddress(values);
                    break;
                default:
                    System.out.println("Unknown data type: " + dataType);
                    break;
            }
        }
    }

    private static void insertFamilyMember(String[] values) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO FamilyMembers (name, birth_date, death_date, is_deceased, current_residence, client_id) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, values[0]);
            statement.setDate(2, java.sql.Date.valueOf(values[1])); // Use java.sql.Date
            if (!values[2].equals("null")) {
                statement.setDate(3, java.sql.Date.valueOf(values[2])); // Use java.sql.Date
            } else {
                statement.setNull(3, Types.DATE);
            }
            statement.setBoolean(4, Boolean.parseBoolean(values[3]));
            statement.setString(5, values[4]);
            statement.setInt(6, Integer.parseInt(values[5]));
            statement.executeUpdate();
            statement.close();
            System.out.println("Family member inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void insertRelationship(String[] values) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Relationships (member_id, related_member_id, relation_type) VALUES (?, ?, ?)");
            statement.setInt(1, Integer.parseInt(values[0]));
            statement.setInt(2, Integer.parseInt(values[1]));
            statement.setString(3, values[2]);
            statement.executeUpdate();
            statement.close();
            System.out.println("Relationship inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertEvent(String[] values) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Events (event_date, event_type, member_id) VALUES (?, ?, ?)");
            statement.setDate(1, java.sql.Date.valueOf(values[0])); // Parse date string into java.sql.Date
            statement.setString(2, values[1]);
            statement.setInt(3, Integer.parseInt(values[2]));
            statement.executeUpdate();
            statement.close();
            System.out.println("Event inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void insertAddress(String[] values) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Addresses (city, member_id) VALUES (?, ?)");
            statement.setString(1, values[0]);
            statement.setInt(2, Integer.parseInt(values[1]));
            statement.executeUpdate();
            statement.close();
            System.out.println("Address inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    







    public static int insertClient(String clientName) {
        int clientId = getNextClientId();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Clients(client_id, client_name) VALUES (?, ?)");
            statement.setInt(1, clientId);
            statement.setString(2, clientName);
            statement.executeUpdate();
            statement.close();
            System.out.println("Client inserted successfully. Client ID: " + clientId);
        } catch (SQLException e) {
            e.printStackTrace();
            clientId = -1; // Error occurred, set ID to -1
        }
        return clientId;
    }

    public static int getNextMemberId() {
        int nextMemberId = 1; // Start from ID 1
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(member_id) AS max_id FROM FamilyMembers");
            if (resultSet.next()) {
                nextMemberId = resultSet.getInt("max_id") + 1;
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextMemberId;
    }


    public static int getNextRelationshipId() {
        int nextRelationshipId = 1; // Start from ID 1
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(relationship_id) AS max_id FROM Relationships");
            if (resultSet.next()) {
                nextRelationshipId = resultSet.getInt("max_id") + 1;
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextRelationshipId;
    }

    public static int insertFamilyMember(String name, Date birthDate, Date deathDate, boolean isDeceased, String currentResidence, int clientId) {
        int memberId = getNextMemberId();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO FamilyMembers (member_id, name, birth_date, death_date, is_deceased, current_residence, client_id) VALUES (?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, memberId);
            statement.setString(2, name);
            statement.setDate(3, birthDate != null ? new java.sql.Date(birthDate.getTime()) : null);
            statement.setDate(4, deathDate != null ? new java.sql.Date(deathDate.getTime()) : null);
            statement.setBoolean(5, isDeceased);
            statement.setString(6, currentResidence);
            statement.setInt(7, clientId);
            statement.executeUpdate();
            statement.close();
            System.out.println("Family member inserted successfully. Member ID: " + memberId);
            return memberId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public static int insertRelationship(int memberId, int relatedMemberId, String relationType) {
        int relationshipId = getNextRelationshipId();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Relationships (relationship_id, member_id, related_member_id, relation_type) VALUES (?, ?, ?, ?)");
            statement.setInt(1, relationshipId);
            statement.setInt(2, memberId);
            statement.setInt(3, relatedMemberId);
            statement.setString(4, relationType);
            statement.executeUpdate();
            statement.close();
            System.out.println("Relationship inserted successfully. Relationship ID: " + relationshipId);
            return relationshipId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public static int insertEvent(Date eventDate, String eventType, int memberId) {
        int eventId = getNextEventId();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Events (event_id, event_date, event_type, member_id) VALUES (?, ?, ?, ?)");
            statement.setInt(1, eventId);
            statement.setDate(2, eventDate != null ? new java.sql.Date(eventDate.getTime()) : null);
            statement.setString(3, eventType);
            statement.setInt(4, memberId);
            statement.executeUpdate();
            statement.close();
            System.out.println("Event inserted successfully for " + getMemberName(memberId) + ". Event ID: " + eventId);
            return eventId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    

    public static int getNextAddressId() {
        int nextAddressId = 1; // Start from ID 1
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(address_id) AS max_id FROM Addresses");
            if (resultSet.next()) {
                nextAddressId = resultSet.getInt("max_id") + 1;
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextAddressId;
    }

    public static int insertAddress(String city, int memberId) {
        int addressId = getNextAddressId();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Addresses(address_id, city, member_id) VALUES (?, ?, ?)");
            statement.setInt(1, addressId);
            statement.setString(2, city);
            statement.setInt(3, memberId);
            statement.executeUpdate();
            statement.close();
            System.out.println("Address inserted successfully. Address ID: " + addressId);
        } catch (SQLException e) {
            e.printStackTrace();
            addressId = -1; // Error occurred, set ID to -1
        }
        return addressId;
    }

    public static int getNextEventId() {
        int nextEventId = 1; // Start from ID 1
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(event_id) AS max_id FROM Events");
            if (resultSet.next()) {
                nextEventId = resultSet.getInt("max_id") + 1;
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextEventId;
    }



    public static String getClientName(int clientId) {
        String clientName = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT client_name FROM Clients WHERE client_id = ?");
            statement.setInt(1, clientId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                clientName = resultSet.getString("client_name");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientName;
    }

    public static String getMemberName(int memberId) {
        String memberName = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT name FROM FamilyMembers WHERE member_id = ?");
            statement.setInt(1, memberId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                memberName = resultSet.getString("name");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberName;
    }
    

    public static int getMemberIdByNameAndBirthDate(String name, Date birthDate) {
        int memberId = -1; // Default to -1 if not found
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT member_id FROM FamilyMembers WHERE name = ? AND birth_date = ?");
            statement.setString(1, name);
            statement.setDate(2, birthDate != null ? new java.sql.Date(birthDate.getTime()) : null);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                memberId = resultSet.getInt("member_id");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberId;
    }
    
    

    public static void main(String[] args) {
        int clientId1 = insertClient("John Doe");
        if (clientId1 != -1) {
            System.out.println("Client inserted successfully. Client ID: " + clientId1);
        } else {
            System.out.println("Failed to insert client.");
        }
    
        int clientId2 = insertClient("Jane Smith");
        if (clientId2 != -1) {
            System.out.println("Client inserted successfully. Client ID: " + clientId2);
        } else {
            System.out.println("Failed to insert client.");
        }
    
        int memberId1 = getMemberIdByNameAndBirthDate("John Doe", null);
        int memberId2 = getMemberIdByNameAndBirthDate("Jane Smith", null);
    
        insertFamilyMember("John Doe", new Date(), null, false, "New York", clientId1);
        if (memberId1 != -1) {
            System.out.println("Family member inserted successfully. Member ID: " + memberId1);
        } else {
            System.out.println("Failed to insert family member.");
        }
    
        insertFamilyMember("Jane Smith", new Date(), null, false, "Los Angeles", clientId2);
        if (memberId2 != -1) {
            System.out.println("Family member inserted successfully. Member ID: " + memberId2);
        } else {
            System.out.println("Failed to insert family member.");
        }
    
        insertRelationship(memberId1, memberId2, "Married");
        System.out.println("Relationship inserted successfully.");
    
        insertAddress("New York", memberId1);
        System.out.println("Address inserted successfully.");
    
        insertAddress("Los Angeles", memberId2);
        System.out.println("Address inserted successfully.");
    
        insertEvent(new Date(), "Wedding", memberId1);
        System.out.println("Event inserted successfully for " + getMemberName(memberId1));
    
        insertEvent(new Date(), "Wedding", memberId2);
        System.out.println("Event inserted successfully for " + getMemberName(memberId2));
    
        // Test getters
        System.out.println("Client Name: " + getClientName(clientId1));
        System.out.println("Member Name: " + getMemberName(memberId1));
    
        // Test insertions into Relationships
        int relationshipId = insertRelationship(memberId1, memberId2, "Married");
        if (relationshipId != -1) {
            System.out.println("Relationship inserted successfully. Relationship ID: " + relationshipId);
        } else {
            System.out.println("Failed to insert relationship.");
        }
    
        // Test insertions into EventAttendees
        int eventId = insertEvent(new Date(), "Wedding", memberId1);
        if (eventId != -1) {
            System.out.println("Event inserted successfully for " + getMemberName(memberId1) + ". Event ID: " + eventId);
        } else {
            System.out.println("Failed to insert event.");
        }
    }
}    