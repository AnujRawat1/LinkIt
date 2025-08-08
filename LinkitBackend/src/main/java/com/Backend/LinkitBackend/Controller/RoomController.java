package com.Backend.LinkitBackend.Controller;

import com.Backend.LinkitBackend.Constants.AppConstants;
import com.Backend.LinkitBackend.Entity.JoinRoomRequest;
import com.Backend.LinkitBackend.Entity.MessageBody;
import com.Backend.LinkitBackend.Repository.RoomRepo;
import com.Backend.LinkitBackend.Services.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@RestController
@RequestMapping("/api/room")
@Slf4j
@CrossOrigin(origins = AppConstants.FRONTEND_URL_PROD + "," + AppConstants.FRONTEND_URL_DEV)
public class RoomController {

    @Autowired
    private RoomService roomServive;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/createRoom")
    public ResponseEntity<MessageBody> createRoom(@RequestBody JoinRoomRequest requestBody) {
        try{
            MessageBody room =  roomServive.createRoom(requestBody.getName());
            return ResponseEntity.ok(room); // Placeholder for actual validation logic
        }catch (Exception e){
            log.error("Error creating room: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/joinRoom")
    public ResponseEntity<MessageBody> joinRoom(@RequestBody JoinRoomRequest request) {
        String roomId = request.getRoomId();
        String name = request.getName();
        boolean isValid = (roomRepo.findByRoomId(roomId) != null) ;
        if (isValid) { // Room Exists
            log.info("Room ID is valid: " + roomId);
            MessageBody room = roomServive.getRoom(roomId, name);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, room);
            return ResponseEntity.ok(room);
        } else{
            log.error("Room ID is invalid or Room Does not Exists: " + roomId);
            return ResponseEntity.status(204).body(null);
        }
    }

    @GetMapping("/getContent")
    public ResponseEntity<String> getContent(@RequestParam String roomId) {
        boolean isValid = (roomRepo.findByRoomId(roomId) != null) ;
        if (isValid) { // Room Exists
            MessageBody room = roomServive.getRoom(roomId);
            log.info("Content fetched for Room ID: " + roomId);
            return ResponseEntity.ok(room.getContent());
        } else{
            return ResponseEntity.status(204).body(null);
        }
    }

    @GetMapping("/getParticipants")
    public ResponseEntity<List<String>> getParticipants(@RequestParam String roomId) {
        boolean isValid = (roomRepo.findByRoomId(roomId) != null) ;
        if (isValid) { // Room Exists
            MessageBody room = roomServive.getRoom(roomId);
            return ResponseEntity.ok(room.getParticipants());
        } else{
            return ResponseEntity.status(204).body(null);
        }
    }



    // RoomController.java
    @DeleteMapping("/removeParticipant")
    public ResponseEntity<MessageBody> removeParticipant(@RequestParam String roomId, @RequestParam String participantName) {
        boolean isValid = (roomRepo.findByRoomId(roomId) != null);
        if (isValid) { // Room Exists
            MessageBody room = roomServive.getRoom(roomId);
            List<String> participants = room.getParticipants();
            if (participants.remove(participantName)) {
                room.setParticipants(participants);
                if (participants.isEmpty()) {
                    // Delete the room if no participants remain
                    roomRepo.delete(room);
                    log.info("Room deleted as no participants remain: " + roomId);
                    messagingTemplate.convertAndSend("/topic/room/" + roomId, Optional.ofNullable(null)); // Notify clients (optional)
                    return ResponseEntity.ok(null); // Indicate room is deleted
                } else {
                    // Save updated room if participants remain
                    roomRepo.save(room);
                    log.info("Participant removed: " + participantName + " from Room ID: " + roomId);
                    messagingTemplate.convertAndSend("/topic/room/" + roomId, room);
                    return ResponseEntity.ok(room);
                }
            } else {
                return ResponseEntity.status(404).body(null); // Participant not found
            }
        } else {
            return ResponseEntity.status(204).body(null); // Room not found
        }
    }

    @GetMapping("/getFileNames")
    public ResponseEntity<List<String>> getFileNames(@RequestParam String roomId) {
        boolean isValid = (roomRepo.findByRoomId(roomId) != null) ;
        if (isValid) { // Room Exists
            MessageBody room = roomServive.getRoom(roomId);
            return ResponseEntity.ok(room.getFileNames());
        } else{
            return ResponseEntity.status(204).body(null);
        }
    }
    
}
