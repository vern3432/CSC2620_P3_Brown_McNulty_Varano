package com.familytree;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyTreeContainer {
    private HashMap<Integer, FamilyMember> members;

    public FamilyTreeContainer(List<FamilyMember> members,HashMap<Integer,HashMap<String,Integer>> relationshipsMap) {
        this.members = new HashMap<Integer, FamilyMember>();
        for (FamilyMember member : members) {
            this.members.put(member.getId(), member);
        }
        for (Map.Entry<Integer, HashMap<String, Integer>> entry : relationshipsMap.entrySet()) {
            int memberId = entry.getKey();
            HashMap<String, Integer> relations = entry.getValue();
            System.out.println("Member ID: " + memberId);
            // Traverse the inner map for each member
            for (Map.Entry<String, Integer> relationEntry : relations.entrySet()) {
                String relationType = relationEntry.getKey();
                int relatedMemberId = relationEntry.getValue();
                // Process the inner map key and value
                System.out.println("Relationship Type: " + relationType);
                System.out.println("Related Member ID: " + relatedMemberId);
                switch (relationType) {
                    case "marriedto":
                        System.out.println("    " + memberId + " is married to " + relatedMemberId);
                        this.members.get(memberId).setSpouse(relatedMemberId);
                        break;
                    case "parentof":
                        System.out.println("    " + memberId + " is parent of " + relatedMemberId);

                        break;
                    default:
                        System.out.println("    Unknown relationship type: " + relationType);
                        break;
                }



            }
        }
    }


    


    public void addMember(FamilyMember member) {
        members.put(member.hashCode(), member);
    }

    public void removeMember(FamilyMember member) {
        members.remove(member.hashCode());
    }

    @Override
    public String toString() {
        return "FamilyTreeContainer [members=" + members + "]";
    }

    // Other methods as needed

    // Database function to create the table for FamilyTreeContainer

}
