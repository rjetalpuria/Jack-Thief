package com.example.jackthief.externalApiResponses;

import com.example.jackthief.models.Card;
import com.example.jackthief.models.Pile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class PileListResponse extends DeckOfCardApiResponse {
    private JSONObject piles;

    public JSONObject getPiles() {
        return piles;
    }

    public PileListResponse setPiles(JSONObject piles) {
        this.piles = piles;
        return this;
    }

    public ArrayList<Card> cardsInPile (String username) throws ParseException, JsonProcessingException {
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(piles.toJSONString());

        ObjectMapper objectMapper = new ObjectMapper();
        Pile pile = objectMapper.readValue(obj.get(username).toString(), Pile.class);

        return pile.getCards();
    }

    @Override
    public String toString() {
        return "PileListResponse{" +
                "piles=" + piles +
                ", success='" + success + '\'' +
                ", deck_id='" + deck_id + '\'' +
                ", remaining=" + remaining +
                '}';
    }
}
