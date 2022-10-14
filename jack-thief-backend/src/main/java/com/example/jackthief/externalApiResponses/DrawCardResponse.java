package com.example.jackthief.externalApiResponses;

import com.example.jackthief.models.Card;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties({"error"})
public class DrawCardResponse extends DeckOfCardApiResponse {
    private ArrayList<Card> cards;
    public ArrayList<Card> getCards() {
        return cards;
    }

    public DrawCardResponse setCards(ArrayList<Card> cards) {
        this.cards = cards;
        return this;
    }
}
