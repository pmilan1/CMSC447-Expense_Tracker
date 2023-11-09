package com.example.rammzexpensetracker.ui;

public class User {

    public User(String id) {
        this.id = id;
    }

    public String GetID() {
        return id;
    }

    public void SetID(String id) {
        this.id = id;
    }

    private String id;
}
