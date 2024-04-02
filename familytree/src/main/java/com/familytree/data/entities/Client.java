package com.familytree.data.entities;

public class Client {
    private int clientId;
    private String name;
    private String userName;

    public Client(int clientId, String name, String userName) {
        this.clientId = clientId;
        this.name = name;
        this.userName = userName;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
