package org.example.reviewservice.mapper.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ReviewResponseDTO {
    private UUID id;
    private UUID attractionId;
    private Long userId;
    private int rating;
    private String comment;
}
