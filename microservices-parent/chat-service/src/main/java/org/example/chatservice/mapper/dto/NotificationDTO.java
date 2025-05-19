package org.example.chatservice.mapper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.chatservice.mapper.entity.ChatRoom;
import org.springframework.messaging.core.MessagePostProcessor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private ChatRoom chatRoom;
    private String message;
}
