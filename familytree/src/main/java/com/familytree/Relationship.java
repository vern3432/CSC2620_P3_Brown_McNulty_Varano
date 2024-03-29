package com.familytree;

public class Relationship {

    private int member1;
    private int member2;
    private String type;

    public Relationship(int member1, int member2, String type) {
        this.member1 = member1;
        this.member2 = member2;
        this.type = type;
    }

    public void setMember1(int member1) {
        this.member1 = member1;
    }

    public void setMember2(int member2) {
        this.member2 = member2;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMember1() {
        return member1;
    }

    public int getMember2() {
        return member2;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Relationship [member1=" + member1 + ", member2=" + member2 + ", type=" + type + "]";
    }

    // Additional methods if needed
}

enum RelationshipType {
    PARENT_CHILD,
    SPOUSE,
    // Add more relationship types as needed
}
