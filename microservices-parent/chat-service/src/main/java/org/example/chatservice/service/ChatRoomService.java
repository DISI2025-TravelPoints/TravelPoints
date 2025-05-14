package org.example.chatservice.service;

import lombok.RequiredArgsConstructor;
import org.example.chatservice.mapper.entity.ChatRoom;
import org.example.chatservice.mapper.entity.User;
import org.example.chatservice.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public Optional<ChatRoom> findByUserAndAttraction(User user, UUID attractionId) {
        return chatRoomRepository.findBySenderAndAttractionId(user, attractionId);
    }

    public ChatRoom createRoom(User user, UUID attractionId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setSender(user);
        chatRoom.setAttractionId(attractionId);
        chatRoom.setActive(true);
        return chatRoomRepository.save(chatRoom);
    }
}
