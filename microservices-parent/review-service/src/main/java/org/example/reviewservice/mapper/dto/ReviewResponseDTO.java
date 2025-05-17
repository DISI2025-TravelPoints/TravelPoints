package org.example.reviewservice.mapper.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.UUID;

@Data
@Builder
public class ReviewResponseDTO {
    private UUID id;
    private UUID attractionId;
    private Long userId;
    private int rating;
    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

}
