package com.example.jackthief.controllers;

import com.example.jackthief.models.Room;
import com.example.jackthief.models.SseMessage;
import com.example.jackthief.repo.RoomRepo;
import com.example.jackthief.requests.BasicUserInfo;
import com.example.jackthief.responses.CreateJoinResponse;
import com.example.jackthief.responses.UserListResponse;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@CrossOrigin
@RestController
public class RoomController {
    private final RoomRepo roomRepo;
    private final Emitter emitter;
    private final Logger logger = LoggerFactory.getLogger(RoomController.class);

    @Autowired
    RoomController(RoomRepo roomRepo, Emitter emitter){
        this.roomRepo = roomRepo;
        this.emitter = emitter;
    }

    @PostMapping("/create-room")
    public ResponseEntity<CreateJoinResponse> createRoom(@RequestBody BasicUserInfo user) throws IOException, InterruptedException {
        ObjectId room_id = roomRepo.create(user.getUsername());
        logger.info("New room created with room id: " + room_id + " by user: " + user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(new CreateJoinResponse()
                .setRoomId(room_id.toString())
                .setMessage("Room Created Successfully!")
        );
    }

    @PostMapping("/join-room/{roomId}")
    public ResponseEntity<CreateJoinResponse> joinRoom(@PathVariable ObjectId roomId,
                                                       @RequestBody BasicUserInfo user){
        logger.info("Incoming Join-Room request for room: " + roomId + " from user: " + user.getUsername());
        if(user.getUsername() == null || user.getUsername().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CreateJoinResponse()
                    .setRoomId(null).setMessage("Empty username provided")
            );
        }

        if(roomRepo.getRoom(roomId).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CreateJoinResponse()
                    .setRoomId(roomId.toString())
                    .setMessage("Room not found for the provided room id")
            );
        }
        roomRepo.join(roomId, user.getUsername());
        SseMessage message = new SseMessage().setMessage("update-userList").setUser("all");
        emitter.sendUpdate(message);

        return ResponseEntity.status(HttpStatus.OK).body(new CreateJoinResponse()
                .setRoomId(roomId.toString())
                .setMessage("Joined room successfully")
        );
    }

    @GetMapping("/user-list/{roomId}")
    public ResponseEntity<UserListResponse> getUserList(@PathVariable ObjectId roomId){
        logger.info("Incoming User-List request for room: " + roomId);
        Optional<Room> roomOptional = roomRepo.getRoom(roomId);
        if(roomOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(new UserListResponse()
                    .setUsers(roomOptional.get().getUsers())
                    .setMessage("Users found")
            );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserListResponse()
                    .setMessage("Room not found")
            );
        }
    }
}
