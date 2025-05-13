package org.example.chatservice.mapper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
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
    @JoinColumn(name="sender_id", nullable=false)
    private User senderId;
    @ManyToOne
    @JoinColumn(name="recipient_id", nullable=false)
    private User recipientId;
    private String content;
    private Timestamp timestamp;
}
