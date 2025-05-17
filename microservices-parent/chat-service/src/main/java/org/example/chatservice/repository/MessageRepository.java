package org.example.chatservice.repository;

import org.example.chatservice.mapper.entity.ChatRoom;
import org.example.chatservice.mapper.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
}
