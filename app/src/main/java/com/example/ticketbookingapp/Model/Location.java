package com.example.ticketbookingapp.Model;

public class Location {
    private int id;
    private String Name;

    public Location(){

    }

    @Override
    public String toString() {
        return Name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
