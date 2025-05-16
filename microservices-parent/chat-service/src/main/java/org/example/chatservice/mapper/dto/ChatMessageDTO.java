package org.example.chatservice.mapper.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class ChatMessageDTO {
    private UUID id;
    private UUID chatRoomId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Timestamp timestamp;
}
