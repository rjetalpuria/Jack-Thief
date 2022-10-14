package com.example.jackthief.responses;

public class CreateJoinResponse {
    private String roomIdStr;
    private String message;

    public String getMessage() {
        return message;
    }

    public CreateJoinResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getRoomIdStr() {
        return roomIdStr;
    }

    public CreateJoinResponse setRoomId(String roomIdStr) {
        this.roomIdStr = roomIdStr;
        return this;
    }
}
