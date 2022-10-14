package com.example.jackthief.controllers;

import com.example.jackthief.models.SseMessage;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@CrossOrigin
@RestController
public class Emitter {
    private final List<SseEmitter> emitters;

    Emitter(){
        this.emitters = new CopyOnWriteArrayList<>();
    }

    @RequestMapping(value = "/subscribe", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe(){
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try{
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e){
            e.printStackTrace();
        }
        sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));

        emitters.add(sseEmitter);
        return sseEmitter;
    }

    @PostMapping("/sendUpdate")
    public void sendUpdate(@RequestParam String update){
        SseMessage message = new SseMessage().setMessage(update).setUser("all");
        for(SseEmitter emitter : emitters){
            try {
                emitter.send(SseEmitter.event().name("update").data(message, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
    public void sendUpdate(SseMessage message){
        for(SseEmitter emitter : emitters){
            try {
                emitter.send(SseEmitter.event().name("update").data(message, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
