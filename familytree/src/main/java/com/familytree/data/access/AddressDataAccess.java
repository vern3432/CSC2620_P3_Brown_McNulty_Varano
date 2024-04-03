package com.familytree.data.access;

import com.familytree.data.entities.Address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddressDataAccess {

    
    /** 
     * @param memberId
     * @param city
     * @param state
     * @param connection
     * @return Address
     * @throws SQLException
     */
    public static Address create(int memberId, String city, String state, Connection connection) throws SQLException {
        final String addressSql = "INSERT INTO Addresses (state, city, member_id) VALUES (?, ?, ?)";
        var result = new Address(memberId, city, state);
        try (PreparedStatement addressStmt = connection.prepareStatement(addressSql)) {
            addressStmt.setString(1, state);
            addressStmt.setString(2, city);
            addressStmt.setInt(3, memberId);
            addressStmt.executeUpdate();
        }
        return result;
    }

}
