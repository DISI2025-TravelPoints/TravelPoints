package org.example.chatservice.mapper.dto;

import lombok.Builder;
import lombok.Data;
import org.example.chatservice.mapper.entity.UserRole;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class ChatMessageDTO {
    private String content;
    private UserRole senderRole;
    private UserRole recipientRole;
}
