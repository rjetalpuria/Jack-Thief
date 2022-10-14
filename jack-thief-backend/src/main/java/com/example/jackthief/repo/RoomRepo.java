package com.example.jackthief.repo;

import com.example.jackthief.models.Room;
import com.mongodb.MongoException;
import com.mongodb.client.*;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class RoomRepo {
    CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
    CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
    private final String uri = "mongodb+srv://rushi:1234@cluster0.1htib6h.mongodb.net/?retryWrites=true&w=majority";
    private final MongoClient mongoClient = MongoClients.create(uri);
    private final MongoDatabase database = mongoClient.getDatabase("jack-thief").withCodecRegistry(pojoCodecRegistry);
//    private final MongoCollection<org.bson.Document> collection = database.getCollection("room");
    private final MongoCollection<Room> roomCollection = database.getCollection("room", Room.class);

    private final CardsRepo cardsRepo;

    @Autowired
    public RoomRepo(CardsRepo cardsRepo){
        this.cardsRepo = cardsRepo;
    }

    public ObjectId create(String username) throws IOException, InterruptedException {
        // create a new room: generate a new object id and enter the creator-user into it
        Room new_room = new Room().setId(new ObjectId())
                .setUsers(new ArrayList<>(Collections.singletonList(username)));
        // assign a new deck to this room
        String deckId = cardsRepo.generateDeck();
        new_room.setDeck_id(deckId);
        roomCollection.insertOne(new_room); // insert the new room into the database
        return new_room.getId();
    }

    public Long join(ObjectId roomId, String username){
        Document query = new Document().append("_id", roomId);
        Bson updates = Updates.combine(
                Updates.addToSet("users", username)
        );
        try {
            UpdateResult result = roomCollection.updateOne(query, updates);
            return result.getModifiedCount();
        } catch (MongoException me){
            System.out.println("Unable to update due to an error: " +  me);
        }
        return (long)-1;
    }

    public Optional<Room> getRoom(ObjectId roomId){
        Document query = new Document().append("_id", roomId);
        Room room = roomCollection.find(query).first();
        if(room != null)
            return Optional.of(room);
        else
            return Optional.empty();
    }
}
