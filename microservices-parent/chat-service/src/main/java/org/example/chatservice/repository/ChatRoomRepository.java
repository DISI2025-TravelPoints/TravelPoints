package org.example.chatservice.repository;

import org.example.chatservice.mapper.entity.ChatRoom;
import org.example.chatservice.mapper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    Optional<ChatRoom> findByTouristAndAttractionId(User tourist, UUID attractionId);
}
