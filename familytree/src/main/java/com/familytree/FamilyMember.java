package com.familytree;

import java.util.Date;

public class FamilyMember {
    private String name;
    private Date birthDate;
    private Date deathDate;
    private boolean isDeceased;
    private String currentResidence;

    public FamilyMember(String name, Date birthDate, Date deathDate, boolean isDeceased, String currentResidence) {
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
