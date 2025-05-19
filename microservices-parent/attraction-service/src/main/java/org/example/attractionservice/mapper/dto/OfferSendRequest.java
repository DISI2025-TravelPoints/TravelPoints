package org.example.attractionservice.mapper.dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OfferSendRequest {
    private String title;
    private String description;
    private LocalDateTime validUntil;
    private UUID attractionId;
    private List<Long> userIds;
}
