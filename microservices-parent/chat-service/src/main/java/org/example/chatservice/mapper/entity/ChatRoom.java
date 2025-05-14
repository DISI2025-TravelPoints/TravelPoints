package org.example.chatservice.mapper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name="chatroom")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @ManyToOne
    @JoinColumn(name="sender", nullable=false)
    private User sender;
    @ManyToOne
    @JoinColumn(name="recipient") // can be null when first creating a chatroom
    private User recipient;
    private UUID attractionId;
    private Boolean active;
}
