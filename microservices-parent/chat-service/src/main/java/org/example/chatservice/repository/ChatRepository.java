package org.example.chatservice.repository;

import lombok.NoArgsConstructor;
import org.example.chatservice.mapper.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Message, UUID> {
}
