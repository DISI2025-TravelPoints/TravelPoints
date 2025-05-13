package org.example.chatservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.chatservice.mapper.dto.UserSyncDTO;
import org.example.chatservice.service.ChatService;
import org.example.chatservice.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;

    @PostMapping("/sync")
    public void syncUsers(@RequestBody UserSyncDTO request){
        userService.syncUsers(request);
    }
}
