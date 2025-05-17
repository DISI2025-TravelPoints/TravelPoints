package org.example.chatservice.mapper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;
    @ManyToOne
    @JoinColumn(name="chat_room", nullable=false)
    private ChatRoom chatRoom;
    @ManyToOne
    @JoinColumn(name="sender")
    private User sender;
    private String content;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
