package com.example.jackthief.models;

import java.util.ArrayList;

public class Pile {
    ArrayList<Card> cards;
    String remaining;

    public ArrayList<Card> getCards() {
        return cards;
    }

    public Pile setCards(ArrayList<Card> cards) {
        this.cards = cards;
        return this;
    }

    public String getRemaining() {
        return remaining;
    }

    public Pile setRemaining(String remaining) {
        this.remaining = remaining;
        return this;
    }
}
