package com.example.jackthief.requests;

public class PickCardRequest {
    private String username;
    private String fromUser;
    private Integer cardNumber;

    public String getUsername() {
        return username;
    }

    public PickCardRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getFromUser() {
        return fromUser;
    }

    public PickCardRequest setFromUser(String fromUser) {
        this.fromUser = fromUser;
        return this;
    }

    public Integer getCardNumber() {
        return cardNumber;
    }

    public PickCardRequest setCardNumber(Integer cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }
}
