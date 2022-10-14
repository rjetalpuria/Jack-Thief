package com.example.jackthief.controllers;

import com.example.jackthief.models.Card;
import com.example.jackthief.models.Room;
import com.example.jackthief.models.SseMessage;
import com.example.jackthief.repo.CardsRepo;
import com.example.jackthief.repo.RoomRepo;
import com.example.jackthief.requests.BasicUserInfo;
import com.example.jackthief.requests.PickCardRequest;
import com.example.jackthief.responses.UserPileResponse;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@CrossOrigin
@RestController
public class GameController {
    private final RoomRepo roomRepo;
    private final CardsRepo cardsRepo;
    private final Emitter emitter;
    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    GameController(RoomRepo roomRepo, CardsRepo cardsRepo, Emitter emitter){
        this.roomRepo = roomRepo;
        this.cardsRepo = cardsRepo;
        this.emitter = emitter;
    }

    @GetMapping("/start-game/{roomId}")
    public ResponseEntity<String> startGame(@PathVariable ObjectId roomId) throws IOException, InterruptedException {

        logger.info("Incoming start game request for " + roomId);
        // distribute cards among players in the room
        Optional<Room> roomOptional = roomRepo.getRoom(roomId);
        if(roomOptional.isPresent()){
            Room room = roomOptional.get();
            cardsRepo.distributeCards(room);

            // send SSE to the all users
            SseMessage message = new SseMessage().setMessage("game-started").setUser("all");
            emitter.sendUpdate(message);
        }

        // return each player's pile
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @PostMapping("/get-pile/{roomId}")
    public ResponseEntity<UserPileResponse> getPile(@PathVariable ObjectId roomId,
                                                    @RequestBody BasicUserInfo userInfo) throws IOException, ParseException, InterruptedException {
        logger.info("Incoming Get Pile request for roomId: " + roomId + " from user: " + userInfo.getUsername());
        Optional<Room> room = roomRepo.getRoom(roomId);
        if(room.isPresent()){
            ArrayList<Card> cards = cardsRepo.getUserPile(room.get().getDeck_id(), userInfo.getUsername());
            if(cards.size() == 0){
                SseMessage message = new SseMessage().setMessage("winner").setUser(userInfo.getUsername());
                emitter.sendUpdate(message);

                //someone else could have lost
                // everyone has 0 cards and 1 player has 1 card which is JACK
                boolean onlyJack = false;
                int numCards = 0;
                String loserUser = "";
                for(String user : room.get().getUsers()){
                    ArrayList<Card> tempCards = cardsRepo.getUserPile(room.get().getDeck_id(), user);
                    numCards += tempCards.size();
                    if(tempCards.size() == 1 && tempCards.get(0).getValue().equals("JACK")) {
                        loserUser = user;
                        onlyJack = true;
                    }
                }
                if(numCards == 1 && onlyJack){
                    message.setMessage("loser").setUser(loserUser);
                    emitter.sendUpdate(message);
                }
            } else if(cards.size() == 1 && cards.get(0).getValue().equals("JACK")){
                // check if all user's piles are empty
                boolean allEmpty = true;
                for(String user : room.get().getUsers()){
                    if(!user.equals(userInfo.getUsername()) && cardsRepo.getUserPile(room.get().getDeck_id(), user).size() > 0){
                        allEmpty = false;
                        break;
                    }
                }
                if(allEmpty){
                    SseMessage message = new SseMessage().setMessage("loser").setUser(userInfo.getUsername());
                    emitter.sendUpdate(message);
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(new UserPileResponse()
                    .setCards(cards)
                    .setMessage("User Pile Found")
            );
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserPileResponse()
                .setCards(null)
                .setMessage("Room not found")
        );
    }

    @PostMapping("/remove-pairs/{roomId}")
    public ResponseEntity<String> removePairs(@PathVariable ObjectId roomId,
                                               @RequestBody BasicUserInfo userInfo) throws IOException, InterruptedException, ParseException {
        logger.info("Incoming Remove Pairs Request for room: " + roomId + " from user: " + userInfo.getUsername());
        Optional<Room> room = roomRepo.getRoom(roomId);
        if(room.isPresent()){
            ArrayList<Card> cards = cardsRepo.getUserPile(room.get().getDeck_id(), userInfo.getUsername());
            ArrayList<Card> cardsToRemove = cardsRepo.findDuplicates(cards);
            cardsRepo.removeCardsFromPile(cardsToRemove, room.get().getDeck_id(), userInfo.getUsername());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @PostMapping("/pick-card/{roomId}")
    public ResponseEntity<String> pickCard(@PathVariable ObjectId roomId,
                                           @RequestBody PickCardRequest actionInfo) throws IOException, ParseException, InterruptedException {
        logger.info("Incoming Pick Card Request for room: " + roomId + " from user: " + actionInfo.getUsername());
        Optional<Room> room = roomRepo.getRoom(roomId);
        if(room.isPresent()){
            ArrayList<Card> nextUserCards = cardsRepo.getUserPile(room.get().getDeck_id(), actionInfo.getFromUser());
            if(actionInfo.getCardNumber() >= nextUserCards.size()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid card number. " + actionInfo.getFromUser().toUpperCase() + " only has " + nextUserCards.size() + " cards.");
            }
            Card pickedCard = nextUserCards.get(actionInfo.getCardNumber());
            cardsRepo.removeCardsFromPile(new ArrayList<>(Collections.singletonList(pickedCard)), room.get().getDeck_id(), actionInfo.getFromUser());
            cardsRepo.addCardsToPile(new ArrayList<>(Collections.singletonList(pickedCard)), room.get().getDeck_id(), actionInfo.getUsername());

            // send SSE to the pickedFrom user to refresh their cards
            SseMessage message = new SseMessage().setMessage("update-cards").setUser(actionInfo.getFromUser());
            emitter.sendUpdate(message);
        }

        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @Deprecated // since start-game handles starting as well as restarting the game
    @GetMapping("/reset-game/{roomId}")
    public ResponseEntity<String> resetGame(@PathVariable ObjectId roomId) throws IOException, InterruptedException {
        Optional<Room> room = roomRepo.getRoom(roomId);
        System.out.println("Resetting game...");
        if(room.isPresent()){
            cardsRepo.returnAllCardsToDeck(room.get().getDeck_id(), room.get().getUsers());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @PostMapping("/shuffle-pile/{roomId}")
    public ResponseEntity<String> shufflePile(@PathVariable ObjectId roomId,
                                              @RequestBody BasicUserInfo userInfo) throws IOException, InterruptedException {
        logger.info("Incoming Shuffle Pile request for room: " + roomId + " from user: " + userInfo.getUsername());
        Optional<Room> room = roomRepo.getRoom(roomId);
        if(room.isPresent()){
            cardsRepo.shufflePile(room.get().getDeck_id(), userInfo.getUsername());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
