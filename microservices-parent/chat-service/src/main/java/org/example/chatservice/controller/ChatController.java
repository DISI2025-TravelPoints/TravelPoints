package org.example.chatservice.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.example.chatservice.mapper.dto.ContactRequestDTO;
import org.example.chatservice.mapper.dto.NotificationDTO;
import org.example.chatservice.mapper.dto.UserSyncDTO;
import org.example.chatservice.mapper.entity.ChatRoom;
import org.example.chatservice.mapper.entity.User;
import org.example.chatservice.mapper.entity.UserRole;
import org.example.chatservice.service.ChatRoomService;
import org.example.chatservice.service.MessageService;
import org.example.chatservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    @PostMapping("/sync")
    public ResponseEntity<?> syncUsers(@RequestBody UserSyncDTO request){
        userService.syncUsers(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-empty-room")
    public ResponseEntity<?> createEmptyRoom(@RequestBody ContactRequestDTO request){
        Optional<User> user = userService.findUser(request.getEmail());
        if(user.isPresent()){
            // check if there is already a room created for this user
            Optional<ChatRoom> chatRoom = chatRoomService.findByTouristAndAttraction(user.get(), request.getAttraction());
            if(chatRoom.isPresent()){
                // there is already a chat room active
                messagingTemplate.convertAndSend("/notification/admin", new NotificationDTO(chatRoom.get(), request.getMessage()));
                messageService.addMessage(chatRoom.get(),user.get(), request.getMessage());
                return ResponseEntity.ok().build();
            }
            else{
                // no chat room present
                ChatRoom createdChatRoom =  chatRoomService.createRoom(user.get(), request.getAttraction());
                messagingTemplate.convertAndSend("/notification/admin", new NotificationDTO(createdChatRoom, "A new ticket has been submitted!"));
                messageService.addMessage(createdChatRoom,user.get(), request.getMessage());
                return ResponseEntity.status(HttpStatus.CREATED).body(createdChatRoom.getId());
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
    }

    @GetMapping("/get-empty-room")
    public ResponseEntity<?> getEmptyRoom(@RequestParam String email) {
        Optional<User> adminOpt = userService.findUser(email);

        if (adminOpt.isPresent()) {
            User admin = adminOpt.get();
            if (UserRole.Admin.equals(admin.getRole())) {
                List<ChatRoom> chatRoomList = chatRoomService.getRooms(admin.getId());
                return ResponseEntity.ok(chatRoomList);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not an admin");
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
    }

    @GetMapping("/get-tourist-chat-room")
    public ResponseEntity<?> getTouristChatRoom(@RequestParam String email) {
        Optional<User> userOpt = userService.findUser(email);
        if (userOpt.isPresent()) {
            User tourist = userOpt.get();
            List<ChatRoom> chatRoomList = chatRoomService.getTouristRooms(tourist);
            return ResponseEntity.ok(chatRoomList);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
    }

    @PostMapping("/join-room/{roomId}")
    public ResponseEntity<?> joinRoom(@RequestParam String email, @PathVariable UUID roomId) {
            Optional<ChatRoom> chatRoom = chatRoomService.findChatRoom(roomId);

            if(chatRoom.isPresent()){
        Optional<User> adminOpt = userService.findUser(email);
        if (adminOpt.isPresent()) {
            User admin = adminOpt.get();
                chatRoomService.allocateAdminToChatRoom(admin, chatRoom.get());
                return ResponseEntity.ok().build();
            }
        }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
    }

    @GetMapping("/get-room-messages")
    public ResponseEntity<?> getRoomMessages(@RequestParam UUID roomId) {
        Optional<ChatRoom> chatRoom = chatRoomService.findChatRoom(roomId);
        if(chatRoom.isPresent()){
            return ResponseEntity.ok().body(messageService.getMessagesForChatRoom(chatRoom.get()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chat room not found");
    }
}
