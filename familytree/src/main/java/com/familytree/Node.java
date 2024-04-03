
package com.familytree;
import com.familytree.data.entities.FamilyMember;

import java.util.ArrayList;
import java.util.List;
class Node {
    private String name;
    private ArrayList<Node> connections;
    private int x;
    private int y;
    private boolean isCouple;
    private Node partner;
    private FamilyMember member;

    
    /** 
     * Gets family member
     * 
     * @return FamilyMember
     */
    public FamilyMember getMember() {
        return member;
    }

    /**
     * Sets family member
     * 
     * @param member
     */
    public void setMember(FamilyMember member) {
        this.member = member;
    }

    /**
    * Constructs a new Node object with the specified name.
    *
    * @param name the name of the node.
    */
    public Node(String name) {
        this.name = name;
        this.connections = new ArrayList<>();
        this.isCouple = false;
    }

    /**
     * Sets x coordinate
     * 
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets y coordinate
     * 
     * @param y
     */
    public void setY(int y) {
        this.y = y;
    }

    /** 
     * Gets person's name
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Adds node to connection
     * 
     * @param node
     */
    public void addConnection(Node node) {
        connections.add(node);
    }

    /**
     * Gets connections
     * 
     * @return
     */
    public List<Node> getConnections() {
        return connections;
    }

    /**
     * Gets X coordinate
     * 
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Gets y coordinate
     * 
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
    * Checks if node represents a couple
    *
    * @return true if the node represents a couple, and false otherwise
    */
    public boolean isCouple() {
        return isCouple;
    }

    /**
     * Sets whether node represents couple
     * 
     * @param couple
     */
    public void setCouple(boolean couple) {
        isCouple = couple;
    }

    /**
     * Gets marital partner
     * 
     * @return partner
     */
    public Node getPartner() {
        return partner;
    }

    /**
     * Sets marital partner 
     * 
     * @param partner
     */
    public void setPartner(Node partner) {
        this.partner = partner;
    }
}