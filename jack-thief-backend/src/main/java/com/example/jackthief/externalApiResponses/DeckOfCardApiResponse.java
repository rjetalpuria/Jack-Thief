package com.example.jackthief.externalApiResponses;

abstract class DeckOfCardApiResponse {
    protected String success;
    protected String deck_id;
    protected Integer remaining;

    public String getSuccess() {
        return success;
    }

    public DeckOfCardApiResponse setSuccess(String success) {
        this.success = success;
        return this;
    }

    public String getDeck_id() {
        return deck_id;
    }

    public DeckOfCardApiResponse setDeck_id(String deck_id) {
        this.deck_id = deck_id;
        return this;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public DeckOfCardApiResponse setRemaining(Integer remaining) {
        this.remaining = remaining;
        return this;
    }
}
