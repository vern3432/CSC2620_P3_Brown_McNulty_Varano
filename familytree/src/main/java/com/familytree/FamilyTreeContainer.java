package com.familytree;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

public class FamilyTreeContainer {
    private HashMap<Integer, FamilyMember> members;

    public FamilyTreeContainer(List<FamilyMember> members,HashMap<Integer,HashMap<String,Integer>> relaHashMap) {
        this.members = new HashMap<>();
    }

    public void addMember(FamilyMember member) {
        members.put(member.hashCode(), member);
    }

    public void removeMember(FamilyMember member) {
        members.remove(member.hashCode());
    }

    // Other methods as needed

    // Database function to create the table for FamilyTreeContainer

}
