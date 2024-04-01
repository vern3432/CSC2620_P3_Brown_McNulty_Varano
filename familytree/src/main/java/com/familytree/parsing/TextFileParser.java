package com.familytree.parsing;

import com.familytree.FamilyDatabase;
import com.familytree.FamilyMember;
import com.familytree.data.access.FamilyDataAccess;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TextFileParser {

    public static List<String[]> parseTextFile(InputStream file, Connection connection) throws IOException, SQLException, ParseException {
        List<String[]> parsedData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(", ")) {
                    System.out.println(line);

                    String[] tokens = line.split(", ");
                    switch (tokens.length) {
                        case 4:
                            parsedData.add(new String[]{"Address", line});
                            break;
                        case 3:
                            if (tokens[2].matches("\\d{1,2} \\d{1,2} \\d{4}")) {
                                createDeathMember(tokens, connection);
                            } else {
                                parsedData.add(new String[]{"Relationship", line});
                            }
                            break;
                        case 2:
                            if (tokens[1].equals("marriedto")) {
                                parsedData.add(new String[]{"Relationship", line});
                            } else if (tokens[1].equals("parentof")) {
                                parsedData.add(new String[]{"Relationship", line});
                            }
                            break;
                        default:
                            System.err.println("Invalid line: " + line);
                    }
                }
            }
        }
        return parsedData;
    }

    private static void createDeathMember(String[] tokens, Connection connection) throws ParseException, SQLException {
        String name = tokens[0];
        String birthDate = tokens[1];
        String deathDate = tokens[2];

        SimpleDateFormat formatter = new SimpleDateFormat("M d yyyy");
        Date bdate = formatter.parse(birthDate);
        Date ddate = formatter.parse(deathDate);
        var nextMemberId = getNextMemberId(connection);
        var member = new FamilyMember(nextMemberId, name, bdate, ddate, true, null);
        FamilyDataAccess.create(member, connection);
    }

    private static void createAliveMember(String[] tokens, Connection connection) throws ParseException, SQLException {
        String name = tokens[0];
        String birthDate = tokens[1];
        var currentResidence = tokens[2];
        var state = tokens[3];

        SimpleDateFormat formatter = new SimpleDateFormat("M d yyyy");
        Date bdate = formatter.parse(birthDate);
        var nextMemberId = getNextMemberId(connection);
        var member = new FamilyMember(nextMemberId, name, bdate, null, false, currentResidence);
        FamilyDataAccess.create(member, connection);
    }

    private static int getNextMemberId(Connection conn) {
        int nextMemberId = 1; // Start from ID 1
        try {
            Statement statement = conn.createStatement();
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
}