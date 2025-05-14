package org.example.chatservice.service;

import lombok.RequiredArgsConstructor;
import org.example.chatservice.mapper.entity.ChatRoom;
import org.example.chatservice.mapper.entity.Message;
import org.example.chatservice.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public void addMessage(ChatRoom createdChatRoom, String message) {
        messageRepository.save(Message.builder().content(message).chatRoom(createdChatRoom).build());
    }
}
