package com.familytree.data.access;

import java.sql.*;

public class RelationshipDataAccess {

    
    /** 
     * @param memberId
     * @param relatedMemberId
     * @param relationType
     * @param conn
     * @return int
     * @throws SQLException
     */
    public static int create(int memberId, int relatedMemberId, String relationType, Connection conn) throws SQLException {
        int nextRelationshipId = getNextRelationshipId(conn);

        final String SQL = "INSERT INTO Relationships (relationship_id, member_id, related_member_id, relation_type) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, nextRelationshipId);
            pstmt.setInt(2, memberId);
            pstmt.setInt(3, relatedMemberId);
            pstmt.setString(4, relationType);
            pstmt.executeUpdate();
        }
        return nextRelationshipId;
    }


    public static boolean hasRelation(int memberId, int relatedMemberId, Connection conn) throws SQLException {
        final String SQL = "SELECT * FROM Relationships WHERE member_id = ? AND related_member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, relatedMemberId);
            var resultSet = pstmt.executeQuery();
            return resultSet.next();
        }
    }

    private static int getNextRelationshipId(Connection conn) throws SQLException {
        int nextRelationshipId = 1; // Start from ID 1
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(relationship_id) AS max_id FROM Relationships");
        if (resultSet.next()) {
            nextRelationshipId = resultSet.getInt("max_id") + 1;
        }
        resultSet.close();
        statement.close();
        return nextRelationshipId;
    }

}
