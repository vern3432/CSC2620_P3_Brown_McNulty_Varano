package com.familytree;

public class Relationship {

    private int member1;
    private int member2;
    private String type;

    /**
    * Constructs a new Relationship object with the specified members and type.
    *
    * @param member1 the ID of the first member in the relationship.
    * @param member2 the ID of the second member in the relationship.
    * @param type the type of relationship (e.g., friendship, family, professional).
    */
    
    public Relationship(int member1, int member2, String type) {
        this.member1 = member1;
        this.member2 = member2;
        this.type = type;
    }

    
    /** 
     * Sets given person as member 1 
     * @param member1
     */
    public void setMember1(int member1) {
        this.member1 = member1;
    }

    
    /** 
     * Sets given person as member 2
     * @param member2
     */
    public void setMember2(int member2) {
        this.member2 = member2;
    }

    /**
     * Sets type
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets member 1
     * @return member1
     */
    public int getMember1() {
        return member1;
    }
    
    /**
    * Gets Member 2
    * @return member2
    */
    public int getMember2() {
        return member2;
    }

    /**
     * Gets type
     * @return type
     */
    public String getType() {
        return type;
    }


    /**
     * @return String containing a relationship type between two members
     */
    @Override
    public String toString() {
        return "Relationship [member1=" + member1 + ", member2=" + member2 + ", type=" + type + "]";
    }
}

enum RelationshipType {
    PARENT_CHILD,
    SPOUSE,
}
