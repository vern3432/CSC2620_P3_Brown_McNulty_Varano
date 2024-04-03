package com.familytree.data.access;

import com.familytree.data.entities.Client;
import com.familytree.data.entities.FamilyMember;

import java.sql.*;

public class ClientDataAccess {

    
    /** 
     * @param username
     * @param connection
     * @return Client
     * @throws SQLException
     */
    public static Client getByUserName(String username, Connection connection) throws SQLException {
        final String SQL = "SELECT * FROM Clients WHERE UPPER(user_name) = ?";
        Client result = null;
        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
            pstmt.setString(1, username.toUpperCase());
            var resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                result = createFromRow(resultSet);
            }
        }
        return result;
    }

    public static Client getByUserNameAndPassword(String username, String password, Connection connection) throws SQLException {
        final String SQL = "SELECT * FROM Clients WHERE user_name = ? and password = ?";
        Client result = null;
        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            var resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                result = createFromRow(resultSet);
            }
        }
        return result;
    }

    public static Client create(Client client, String password, Connection connection) throws SQLException {
        final String sql = "INSERT INTO Clients "
                + "(client_id, client_name, user_name, password) "
                + "VALUES (?, ?, ?, ?)";

        client.setClientId(getNextId(connection));

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, client.getClientId());
            pstmt.setString(2, client.getName());
            pstmt.setString(3, client.getUserName());
            pstmt.setString(4, password);
            pstmt.executeUpdate();
        }
        return client;
    }

    private static Client createFromRow(ResultSet resultSet) throws SQLException {
        return new Client(
                resultSet.getInt("client_id"),
                resultSet.getString("client_name"),
                resultSet.getString("user_name")
        );
    }

    private static int getNextId(Connection conn) throws SQLException {
        final String SQL = "SELECT MAX(client_id) AS id FROM Clients";
        int nextMemberId = 1; // Start from ID 1
        try(Statement statement = conn.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery(SQL)) {
                if (resultSet.next()) {
                    nextMemberId = resultSet.getInt("id") + 1;
                }
            }
        }
        return nextMemberId;
    }
}
