package com.familytree;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
class Node {
    private String name;
    private ArrayList<Node> connections;
    private int x;
    private int y;
    private boolean isCouple;
    private Node partner;

    public Node(String name) {
        this.name = name;
        this.connections = new ArrayList<>();
        this.isCouple = false;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void addConnection(Node node) {
        connections.add(node);
    }

    public List<Node> getConnections() {
        return connections;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isCouple() {
        return isCouple;
    }

    public void setCouple(boolean couple) {
        isCouple = couple;
    }

    public Node getPartner() {
        return partner;
    }

    public void setPartner(Node partner) {
        this.partner = partner;
    }
}
