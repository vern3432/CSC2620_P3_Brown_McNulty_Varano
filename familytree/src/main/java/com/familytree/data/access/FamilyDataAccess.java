package com.familytree.data.access;

import com.familytree.data.entities.Address;
import com.familytree.data.entities.FamilyMember;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FamilyDataAccess {

    // SQL Statements
    private static final String sortedByClientAndBirthday = "SELECT * FROM FamilyMembers ORDER BY birth_date";

    /**
     * Returns a list of family members sorted by birthday
     *
     * @return list of family members
     */
    public static List<FamilyMember> getAllSortedByBirthday(Connection connection) throws SQLException {
        var statement = connection.createStatement();
        var resultSet = statement.executeQuery(sortedByClientAndBirthday);
        List<FamilyMember> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(createFromRow(resultSet, false));
        }
        return result;
    }

    
    /** 
     * @param name
     * @param connection
     * @return List<FamilyMember>
     * @throws SQLException
     */
    public static List<FamilyMember> listByNameOrderById(String name, Connection connection) throws SQLException {
        String SQL = "SELECT * FROM FamilyMembers AS f  INNER JOIN Addresses as a ON f.member_id = a.member_id ";
        if (name != null && !name.trim().isEmpty()) {
            SQL += "WHERE UPPER(f.name) like  '%" + name.toUpperCase() + "%'";
        }
        SQL += "ORDER BY f.member_id";
        List<FamilyMember> result = new ArrayList<>();
        try (var statement = connection.createStatement()) {
            try(var resultSet = statement.executeQuery(SQL)) {
                while (resultSet.next()) {
                    result.add(createFromRow(resultSet, true));
                }
            }
        }
        return result;
    }

    public static FamilyMember getByNameAndBirthDate(String name, Date birthDate, Connection connection) throws SQLException {
        final String SQL = "SELECT * FROM FamilyMembers WHERE UPPER(name) = ? and birth_date = ?";
        FamilyMember result = null;
        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
            pstmt.setString(1, name.toUpperCase());
            pstmt.setDate(2, new java.sql.Date(birthDate.getTime()));
            var resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                result = createFromRow(resultSet, false);
            }
        }
        return result;
    }

    public static FamilyMember getByName(String name, Connection connection) throws SQLException {
        final String SQL = "SELECT * FROM FamilyMembers WHERE UPPER(name) = ?";
        FamilyMember result = null;
        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
            pstmt.setString(1, name.toUpperCase());
            var resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                result = createFromRow(resultSet, false);
            }
        }
        return result;
    }

    public static FamilyMember create(FamilyMember member, Connection connection) throws SQLException {
        final String sql = "INSERT INTO FamilyMembers "
                + "(member_id, name, birth_date, death_date, is_deceased) "
                + "VALUES (?, ?, ?, ?, ?)";

        member.setId(getNextMemberId(connection));

        java.sql.Date deathDate = null;
        if (member.getDeathDate() != null) {
            deathDate = new java.sql.Date(member.getDeathDate().getTime());
        }
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, member.getId());
            pstmt.setString(2, member.getName());
            pstmt.setDate(3, new java.sql.Date(member.getBirthDate().getTime()));
            pstmt.setDate(4, deathDate);
            pstmt.setBoolean(5, member.isDeceased());
            pstmt.executeUpdate();
        }
        return member;
    }

    private static FamilyMember createFromRow(ResultSet resultSet, boolean includeAddress) throws SQLException {
        var id = resultSet.getInt("member_id");

        var name = resultSet.getString("name");
        var localBirthDate = resultSet.getObject("birth_date", LocalDate.class);
        var localDeathDate = resultSet.getObject("death_date", LocalDate.class);
        var isDeceased = resultSet.getBoolean("is_deceased");

        // Converting to Date
        var birthDate = Date.from(localBirthDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date deathDate = null;
        if (localDeathDate != null) {
            deathDate = Date.from(localDeathDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }
        var result = new FamilyMember(id, name, birthDate, deathDate, isDeceased, null);


        if (includeAddress) {
            var city = resultSet.getString("city");
            var state = resultSet.getString("state");
            result.setAddress(new Address(id, city, state));
        }
        return result;
    }

    private static int getNextMemberId(Connection conn) throws SQLException {
        int nextMemberId = 1; // Start from ID 1
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(member_id) AS max_id FROM FamilyMembers");
        if (resultSet.next()) {
            nextMemberId = resultSet.getInt("max_id") + 1;
        }
        resultSet.close();
        statement.close();
        return nextMemberId;
    }

}
