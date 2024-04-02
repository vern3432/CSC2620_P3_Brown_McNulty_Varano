package com.familytree.parsing;

import com.familytree.data.access.RelationshipDataAccess;
import com.familytree.data.entities.FamilyMember;
import com.familytree.data.access.AddressDataAccess;
import com.familytree.data.access.FamilyDataAccess;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextFileParser {

    public static ParsingResult parse(InputStream file, Connection connection) throws IOException, SQLException, ParseException {
        var result = new ParsingResult();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(", ")) {
                    String[] tokens = line.split(", ");
                    switch (tokens.length) {
                        case 4:
                            if (createAliveMember(tokens, connection)) {
                                result.setCreated(result.getCreated() + 1);
                            } else {
                                result.setDuplicated(result.getDuplicated() + 1);
                            }
                            break;
                        case 3:
                            if (tokens[2].matches("\\d{1,2} \\d{1,2} \\d{4}")) {
                                if (createDeathMember(tokens, connection)) {
                                    result.setCreated(result.getCreated() + 1);
                                } else {
                                    result.setDuplicated(result.getDuplicated() + 1);
                                }
                            } else {
                                if (createRelationship(tokens, connection)) {
                                    result.setRelationshipsCreated(result.getRelationshipsCreated() + 1);
                                } else {
                                    result.setRelationshipsDuplicated(result.getRelationshipsDuplicated() + 1);
                                }
                            }
                            break;
                        case 2:
                            if (tokens[1].equals("marriedto")) {
                                // parsedData.add(new String[]{"Relationship", line});
                            } else if (tokens[1].equals("parentof")) {
                                // parsedData.add(new String[]{"Relationship", line});
                            }
                            break;
                        default:
                            System.err.println("Invalid line: " + line);
                    }
                }
            }
        }
        return result;
    }

    private static boolean createDeathMember(String[] tokens, Connection connection) throws ParseException, SQLException {
        var success = true;
        String name = tokens[0];
        String birthDate = tokens[1];
        String deathDate = tokens[2];

        SimpleDateFormat formatter = new SimpleDateFormat("M d yyyy");
        Date bdate = formatter.parse(birthDate);
        Date ddate = formatter.parse(deathDate);
        if (FamilyDataAccess.getByNameAndBirthDate(name, bdate, connection) != null) {
            return false;
        }
        var member = new FamilyMember(name, bdate, ddate, true);
        FamilyDataAccess.create(member, connection);
        return success;
    }

    private static boolean createAliveMember(String[] tokens, Connection connection) throws ParseException, SQLException {
        var success = true;
        String name = tokens[0];
        String birthDate = tokens[1];
        var city = tokens[2];
        var state = tokens[3];

        SimpleDateFormat formatter = new SimpleDateFormat("M d yyyy");
        Date bdate = formatter.parse(birthDate);
        if (FamilyDataAccess.getByNameAndBirthDate(name, bdate, connection) != null) {
            return false;
        }
        var member = new FamilyMember(name, bdate, null, false);
        member = FamilyDataAccess.create(member, connection);
        member.setAddress(AddressDataAccess.create(member.getId(), city, state, connection));
        return success;
    }

    private static boolean createRelationship(String[] tokens, Connection connection) throws ParseException, SQLException {
        boolean success = true;

        String memberName = tokens[0];
        String relationType = tokens[1];
        String relatedName = tokens[2];

        var member = FamilyDataAccess.getByName(memberName, connection);
        if (member == null) {
            return false; // Member not found
        }
        var relatedMember = FamilyDataAccess.getByName(relatedName, connection);
        if (relatedMember == null) {
            return false; // Member not found
        }
        if (RelationshipDataAccess.hasRelation(member.getId(), relatedMember.getId(), connection)) {
            return false;
        }
        RelationshipDataAccess.create(member.getId(), relatedMember.getId(), relationType, connection);

        return success;
    }
}