package com.example.jackthief.responses;

import java.util.ArrayList;

public class UserListResponse {
    ArrayList<String> users;
    String message;

    public ArrayList<String> getUsers() {
        return users;
    }

    public UserListResponse setUsers(ArrayList<String> users) {
        this.users = users;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public UserListResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}
