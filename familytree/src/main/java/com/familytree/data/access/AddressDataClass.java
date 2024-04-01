package com.familytree.data.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddressDataClass {

    public static void createAddress(int memberId, String city, String state, Connection connection) throws SQLException {
        final String addressSql = "INSERT INTO Addresses (state, city, member_id) VALUES (?, ?)";
        try (PreparedStatement addressStmt = connection.prepareStatement(addressSql)) {
            addressStmt.setString(1, state);
            addressStmt.setString(2, city);
            addressStmt.setInt(3, memberId);
            addressStmt.executeUpdate();
        }

    }

}
