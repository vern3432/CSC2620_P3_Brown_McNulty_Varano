package com.familytree;

import com.familytree.data.entities.FamilyMember;

import java.util.HashMap;
import java.util.List;

public class FamilyTreeContainer {
    private HashMap<Integer, FamilyMember> members;

    public HashMap<Integer, FamilyMember> getMembers() {
        return members;
    }

    public FamilyTreeContainer(List<FamilyMember> members, HashMap<Integer, Relationship> relationshipsMap) {

        this.members = new HashMap<Integer, FamilyMember>();
        for (FamilyMember member : members) {
            this.members.put(member.getId(), member);
        }
        for (Integer key : relationshipsMap.keySet()) {

            int memberId = key;
            System.out.println("Member ID: " + memberId);
            // Traverse the inner map for each member
            String relationType = relationshipsMap.get(key).getType();
            int relatedMemberId = relationshipsMap.get(key).getMember2();
            // Process the inner map key and value
            System.out.println("Relationship Type: " + relationType);
            System.out.println("Related Member ID: " + relatedMemberId);
            switch (relationType.strip()) {
                case "marriedto":
                    System.out.println("    " + this.members.get(memberId).getName() + " is married to "
                            + this.members.get(relatedMemberId).getName());
                    this.members.get(memberId).setSpouse(relatedMemberId);
                    this.members.get(relatedMemberId).setSpouse(memberId);

                    break;

                case "parentof":
                    System.out.println("adding parent");
                    this.members.get(relatedMemberId).addParent(memberId);
                    this.members.get(memberId).addChildren(relatedMemberId);

                    System.out.println("    " + this.members.get(memberId).getName() + " is parent of "
                            + this.members.get(relatedMemberId).getName());

                    break;
                default:
                    System.out.println("Unknown relationship type: " + relationType);
                    break;
            }

            // for (Map.Entry<String, Integer> relationEntry : relations.entrySet()) {

            // }
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
