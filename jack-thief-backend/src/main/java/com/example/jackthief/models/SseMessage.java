package com.example.jackthief.models;

public class SseMessage {
    private String message;
    private String user;

    public String getMessage() {
        return message;
    }

    public SseMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getUser() {
        return user;
    }

    public SseMessage setUser(String user) {
        this.user = user;
        return this;
    }

    @Override
    public String toString() {
        return "SseMessage{" +
                "message='" + message + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
