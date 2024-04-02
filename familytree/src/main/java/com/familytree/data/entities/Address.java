package com.familytree.data.entities;

/**
 * The Address class represents a member's address.
 * It contains information such as the member ID, city, and state.
 *
 * @author maddie
 */
public class Address {
    /** The ID of the member associated with the address. */
    private int memberId;

    /** The city of the address. */
    private String city;

    /** The state of the address. */
    private String state;

    /**
     * Constructs an Address object with the specified member ID, city, and state.
     * @param memberId The ID of the member associated with the address.
     * @param city The city of the address.
     * @param state The state of the address.
     */
    public Address(int memberId, String city, String state) {
        this.memberId = memberId;
        this.city = city;
        this.state = state;
    }

    /**
     * Gets the ID of the member associated with the address.
     * @return The member ID.
     */
    public int getMemberId() {
        return memberId;
    }

    /**
     * Sets the ID of the member associated with the address.
     * @param memberId The member ID to set.
     */
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    /**
     * Gets the city of the address.
     * @return The city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city of the address.
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state of the address.
     * @return The state.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state of the address.
     * @param state The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }
}
