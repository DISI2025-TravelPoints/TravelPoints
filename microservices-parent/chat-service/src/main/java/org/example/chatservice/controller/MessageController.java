package org.example.chatservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.chatservice.mapper.dto.ChatMessageDTO;
import org.example.chatservice.mapper.dto.NotificationDTO;
import org.example.chatservice.mapper.entity.ChatRoom;
import org.example.chatservice.mapper.entity.Message;
import org.example.chatservice.mapper.entity.User;
import org.example.chatservice.service.ChatRoomService;
import org.example.chatservice.service.MessageService;
import org.example.chatservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.Optional;


@Controller
@RestController
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessagingTemplate template;
    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    @MessageMapping("/chatws")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO){
        Optional<User> userSenderOpt = userService.findUser(chatMessageDTO.getSenderEmail());
        Optional<ChatRoom> chatRoomOpt = chatRoomService.findChatRoom(chatMessageDTO.getChatRoomId());

        if(userSenderOpt.isPresent()){
            User userSender = userSenderOpt.get();
            if(chatRoomOpt.isPresent()){
                ChatRoom chatRoom = chatRoomOpt.get();
                if(chatRoom.getTourist().getId().equals(userSender.getId()) ||
                        chatRoom.getAdmin().getId().equals(userSender.getId())){
                    Message message = messageService.addMessage(chatRoom, userSender, chatMessageDTO.getContent());
                    template.convertAndSend("/topic/chatroom." + chatRoom.getId(), message);
                    template.convertAndSend("/notification/messages", new NotificationDTO(chatRoom, chatMessageDTO.getContent()));
                    //also save the message
                }
            }
        }
    }
}
