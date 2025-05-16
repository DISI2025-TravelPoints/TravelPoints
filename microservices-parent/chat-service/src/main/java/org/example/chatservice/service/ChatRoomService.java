package org.example.chatservice.service;

import lombok.RequiredArgsConstructor;
import org.example.chatservice.mapper.entity.ChatRoom;
import org.example.chatservice.mapper.entity.User;
import org.example.chatservice.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public Optional<ChatRoom> findByTouristAndAttraction(User user, UUID attractionId) {
        return chatRoomRepository.findByTouristAndAttractionId(user, attractionId);
    }

    public ChatRoom createRoom(User user, UUID attractionId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setTourist(user);
        chatRoom.setAttractionId(attractionId);
        chatRoom.setActive(true);
        return chatRoomRepository.save(chatRoom);
    }

    /*
        Fetches all the empty rooms and the rooms that are allocated to the admin.
     */
    public List<ChatRoom> getRooms(Long id) {
        return chatRoomRepository.findAll().stream()
                .filter(room -> room.getAdmin() == null || room.getAdmin().getId().equals(id))
                .collect(Collectors.toList());
    }

    public Optional<ChatRoom> findChatRoom(UUID roomId) {
        return chatRoomRepository.findById(roomId);
    }

    public void allocateAdminToChatRoom(User admin, ChatRoom chatRoom) {
        chatRoom.setAdmin(admin);
        chatRoomRepository.save(chatRoom);
    }
}
