package org.example.chatservice.mapper.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ContactRequestDTO {
    private String email;
    private String message;
    private UUID attraction;
}
