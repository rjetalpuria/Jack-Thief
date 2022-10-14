package com.example.jackthief.repo;

import com.example.jackthief.externalApiResponses.IgnoreEverythingResponse;
import com.example.jackthief.externalApiResponses.PileListResponse;
import com.example.jackthief.models.Card;
import com.example.jackthief.models.Room;
import com.example.jackthief.externalApiResponses.DrawCardResponse;
import com.example.jackthief.externalApiResponses.GenerateDeckResponse;
import com.twilio.JsonBodyHandler;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Collections;

@Component
public class CardsRepo {
    private final HttpClient client  = HttpClient.newHttpClient();
    public String generateDeck() throws IOException, InterruptedException {

        // partial deck with Jack of Spades missing
        String url = "https://www.deckofcardsapi.com/api/deck/new/shuffle/?cards=" +
                "AS,2S,3S,4S,5S,6S,7S,8S,9S,0S,QS,KS," + "AH,2H,3H,4H,5H,6H,7H,8H,9H,0H,JH,QH,KH," + // partial deck with Jack of Spades missing
                "AC,2C,3C,4C,5C,6C,7C,8C,9C,0C,JC,QC,KC," + "AD,2D,3D,4D,5D,6D,7D,8D,9D,0D,JD,QD,KD";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
            .header("accept", "application/json")
            .build();
        var response = client.send(request, new JsonBodyHandler<>(GenerateDeckResponse.class));

        return response.body().get().getDeck_id();
    }

    public void shuffleDeck(String deckId) throws IOException, InterruptedException {
        String url = "https://www.deckofcardsapi.com/api/deck/" + deckId + "/shuffle/";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("accept", "application/json")
                .build();
        client.send(request, new JsonBodyHandler<>(IgnoreEverythingResponse.class));
    }

    public void shufflePile(String deckId, String pilename) throws IOException, InterruptedException {
        String url = "https://www.deckofcardsapi.com/api/deck/" + deckId + "/pile/" + pilename + "/shuffle/";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("accept", "application/json")
                .build();
        client.send(request, new JsonBodyHandler<>(IgnoreEverythingResponse.class));
    }
    public ArrayList<Card> drawCards(String deckId, Integer count) throws IOException, InterruptedException {
        String url = "https://www.deckofcardsapi.com/api/deck/" + deckId + "/draw/?count=" + count;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
            .header("accept", "application/json")
            .build();
        var response = client.send(request, new JsonBodyHandler<>(DrawCardResponse.class));
        return response.body().get().getCards();
    }

    public void addCardsToPile(ArrayList<Card> cards, String deckId, String pilename) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        for(Card c : cards){
            sb.append(c.getCode());
            sb.append(",");
        }
        if(!sb.isEmpty())
            sb.setLength(sb.length()-1); // remove trailing comma
        String url = "https://www.deckofcardsapi.com/api/deck/" + deckId + "/pile/" + pilename + "/add/?cards=" + sb;
//        System.out.println("Assigning cards: " + url);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("accept", "application/json")
                .build();
        client.send(request, new JsonBodyHandler<>(IgnoreEverythingResponse.class));
    }

    public void distributeCards(Room room) throws IOException, InterruptedException {
        shuffleDeck(room.getDeck_id());
        // decide how many cards to draw for each person
        Integer cardsPerUser = (int)Math.ceil(51.0 / room.getUsers().size());
        for(int i = 0; i < room.getUsers().size(); ++i){
            ArrayList<Card> pile = drawCards(room.getDeck_id(), cardsPerUser); // draw cards
//            System.out.println("Pile size: " + pile.size());
            addCardsToPile(pile, room.getDeck_id(), room.getUsers().get(i)); // assign drawn cards to user's pile
        }
    }

    public ArrayList<Card> getUserPile(String deckId, String pilename) throws IOException, InterruptedException, ParseException {
           String url = "https://www.deckofcardsapi.com/api/deck/" + deckId + "/pile/" + pilename + "/list/";

           HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                   .header("accept", "application/json")
                   .build();
           var response = client.send(request, new JsonBodyHandler<>(PileListResponse.class));
           return response.body().get().cardsInPile(pilename);
    }

    public void removeCardsFromPile(ArrayList<Card> cardsToRemove, String deckId, String pilename) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        for(Card c : cardsToRemove){
            sb.append(c.getCode());
            sb.append(",");
        }
        if(!sb.isEmpty()){
            sb.setLength(sb.length()-1); // remove trailing comma
            String url = "https://www.deckofcardsapi.com/api/deck/" + deckId + "/pile/" + pilename + "/draw/?cards=" + sb;
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .header("accept", "application/json")
                    .build();
            client.send(request, new JsonBodyHandler<>(IgnoreEverythingResponse.class));
        }
    }

    public ArrayList<Card> findDuplicates(ArrayList<Card> cards){
//        StringBuilder cardsToRemove = new StringBuilder();
        ArrayList<Card> cardsToRemove = new ArrayList<>();
        for(int i = 0; i < cards.size(); ++i){
            for(int j = i+1; j < cards.size(); ++j){
                if(cards.get(i).getValue().equals(cards.get(j).getValue())){
                    cardsToRemove.add(cards.get(i));
//                    cardsToRemove.append(cards.get(i).getCode());
//                    cardsToRemove.append(",");
                    cardsToRemove.add(cards.get(j));
//                    cardsToRemove.append(cards.get(j).getCode());
//                    cardsToRemove.append(",");
                    Collections.swap(cards, j, i+1);
                    i = i+1;
                    break;
                }
            }
        }
//        if(!cardsToRemove.isEmpty())
//            cardsToRemove.setLength(cardsToRemove.length()-1); //remove trailing comma
        return cardsToRemove;
    }

    public void returnAllCardsToDeck(String deckId, ArrayList<String> users) throws IOException, InterruptedException {
        String url = "https://www.deckofcardsapi.com/api/deck/" + deckId + "/return/";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("accept", "application/json")
                .build();
        client.send(request, new JsonBodyHandler<>(IgnoreEverythingResponse.class));

        for(String user : users){
            url = "https://www.deckofcardsapi.com/api/deck/" + deckId + "/pile/" + user + "/return/";
            request = HttpRequest.newBuilder(URI.create(url))
                    .header("accept", "application/json")
                    .build();
            client.send(request, new JsonBodyHandler<>(IgnoreEverythingResponse.class));
        }
    }
}
