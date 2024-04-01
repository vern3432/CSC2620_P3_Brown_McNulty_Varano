package com.familytree.data.access;

import com.familytree.FamilyMember;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
     * @return  list of family members
     */
    public static List<FamilyMember> getAllSortedByBirthday(Connection connection) throws SQLException {
        var statement = connection.createStatement();
        var resultSet =  statement.executeQuery(sortedByClientAndBirthday);
        List<FamilyMember> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(createFamilyMemberFromRow(resultSet));
        }
        return result;
    }

    public static void create(FamilyMember member, Connection connection) throws SQLException {
        final String sql = "INSERT INTO FamilyMembers "
        + "(member_id, name, birth_date, death_date, is_deceased) "
        + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, member.getId());
            pstmt.setString(2, member.getName());
            pstmt.setDate(3, new java.sql.Date(member.getBirthDate().getTime()));
            pstmt.setDate(4, new java.sql.Date(member.getDeathDate().getTime()));
            pstmt.setBoolean(5, member.isDeceased());
            pstmt.executeUpdate();
        }
    }

    private static FamilyMember createFamilyMemberFromRow(ResultSet resultSet) throws SQLException {
        var id = resultSet.getInt("member_id");

        var name = resultSet.getString("name");
        var localBirthDate = resultSet.getObject("birth_date", LocalDate.class);
        var localDeathDate = resultSet.getObject("death_date", LocalDate.class);
        var isDeceased = resultSet.getBoolean("is_deceased");
        var currentResidence = resultSet.getString("current_residence");

        // Converting to Date
        var birthDate = Date.from(localBirthDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date deathDate = null;
        if (localDeathDate != null) {
            deathDate = Date.from(localDeathDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }
        return new FamilyMember(id,name, birthDate, deathDate, isDeceased, currentResidence);
    }
}
