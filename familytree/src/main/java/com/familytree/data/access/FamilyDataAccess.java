package com.familytree.data.access;

import com.familytree.FamilyMember;

import java.sql.Connection;
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
    private final Connection connection;

    public FamilyDataAccess(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns a list of family members sorted by birthday
     *
     * @return  list of family members
     */
    public List<FamilyMember> getAllSortedByBirthday() throws SQLException {
        var statement = connection.createStatement();
        var resultSet =  statement.executeQuery(sortedByClientAndBirthday);
        List<FamilyMember> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(createFamilyMemberFromRow(resultSet));
        }
        return result;
    }


    private FamilyMember createFamilyMemberFromRow(ResultSet resultSet) throws SQLException {
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
