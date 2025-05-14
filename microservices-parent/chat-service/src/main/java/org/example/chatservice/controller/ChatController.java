package org.example.chatservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.chatservice.mapper.dto.ChatMesageDTO;
import org.example.chatservice.mapper.dto.ContactRequestDTO;
import org.example.chatservice.mapper.dto.UserSyncDTO;
import org.example.chatservice.mapper.entity.ChatRoom;
import org.example.chatservice.mapper.entity.User;
import org.example.chatservice.service.ChatRoomService;
import org.example.chatservice.service.MessageService;
import org.example.chatservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
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
        Optional<User> user = userService.findUser(request.getName(), request.getEmail());
        if(user.isPresent()){
            // check if there is already a room created for this user
            Optional<ChatRoom> chatRoom = chatRoomService.findByUserAndAttraction(user.get(), request.getAttraction());
            if(chatRoom.isPresent()){
                // there is already a chat room active
                messageService.addMessage(chatRoom.get(), request.getMessage());
                return ResponseEntity.ok().build();
            }
            else{
                // no chat room present
                ChatRoom createdChatRoom =  chatRoomService.createRoom(user.get(), request.getAttraction());
                messageService.addMessage(createdChatRoom, request.getMessage());
                return ResponseEntity.status(HttpStatus.CREATED).body(createdChatRoom.getId());
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
    }
}
