package com.familytree;

import java.awt.List;
import java.util.ArrayList;
import java.util.Date;

public class FamilyMember {
    private int id;



    public int getId() {
        return id;
    }

    private String name;
    private Date birthDate;
    private Date deathDate;
    private boolean isDeceased;
    private String currentResidence;
    /**
     *
     */
    private ArrayList<Integer> parents=new ArrayList<Integer>();
    private ArrayList<Integer> children=new ArrayList<Integer>();
    private int spouse=-1;
    private int stackLayer=-1;


    public int getSpouse() {
        return spouse;
    }

    public ArrayList<Integer> getParents() {
        return parents;
    }

    public void setParents(ArrayList<Integer> parents) {
        this.parents = parents;
    }

    public ArrayList<Integer> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Integer> children) {
        this.children = children;
    }

    

    public int getStackLayer() {
        return stackLayer;
    }

    public void setStackLayer(int stackLayer) {
        this.stackLayer = stackLayer;
    }

    
    public void setId(int id) {
        this.id = id;
    }

    public void addParent(Integer id) {
        this.parents.add(id);
    }
    public void addChildren(Integer id) {
        this.children.add(id);
    }

    public void setSpouse(int spouse) {
        this.spouse = spouse;
    }

    public FamilyMember(int id, String name, Date birthDate, Date deathDate, boolean isDeceased, String currentResidence) {
        this.id=id;
        this.name = name;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.isDeceased = isDeceased;
        this.currentResidence = currentResidence;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public void setDeceased(boolean deceased) {
        isDeceased = deceased;
    }

    public void setCurrentResidence(String currentResidence) {
        this.currentResidence = currentResidence;
    }

    // Getters
    public String getName() {
        return name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public boolean isDeceased() {
        return isDeceased;
    }

    public String getCurrentResidence() {
        return currentResidence;
    }
}
