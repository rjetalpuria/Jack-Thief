package com.example.jackthief.responses;

import com.example.jackthief.models.Card;

import java.util.ArrayList;

public class UserPileResponse {
    private ArrayList<Card> cards;
    private String message;
    public ArrayList<Card> getCards() {
        return cards;
    }

    public UserPileResponse setCards(ArrayList<Card> cards) {
        this.cards = cards;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public UserPileResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}
