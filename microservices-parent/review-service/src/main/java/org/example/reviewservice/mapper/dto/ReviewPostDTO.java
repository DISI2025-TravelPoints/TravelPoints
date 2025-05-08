package org.example.reviewservice.mapper.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class ReviewPostDTO {
    @NonNull
    private UUID attractionId;
    @Min(1)
    @Max(5)
    private int rating;
    @Size(max = 30)
    private String comment;
}
