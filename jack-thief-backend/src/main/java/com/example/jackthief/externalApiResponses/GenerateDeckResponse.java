package com.example.jackthief.externalApiResponses;

public class GenerateDeckResponse extends DeckOfCardApiResponse {
    private Boolean shuffled;


    public Boolean getShuffled() {
        return shuffled;
    }

    public GenerateDeckResponse setShuffled(Boolean shuffled) {
        this.shuffled = shuffled;
        return this;
    }

    @Override
    public String toString() {
        return "NewDeckResponse{" +
                "success=" + success +
                ", deck_id='" + deck_id + '\'' +
                ", remaining=" + remaining +
                ", shuffled=" + shuffled +
                '}';
    }
}
