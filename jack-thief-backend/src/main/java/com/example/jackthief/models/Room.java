package com.example.jackthief.models;

import org.bson.types.ObjectId;

import java.util.ArrayList;

public class Room {
    private ObjectId id;
    private ArrayList<String> users;
    private String deck_id;
    public ObjectId getId() {
        return id;
    }

    public Room setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public Room setUsers(ArrayList<String> users) {
        this.users = users;
        return this;
    }

    public String getDeck_id() {
        return deck_id;
    }

    public Room setDeck_id(String deck_id) {
        this.deck_id = deck_id;
        return this;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", users=" + users +
                ", deck_id='" + deck_id + '\'' +
                '}';
    }

}
