package org.example.reviewservice.mapper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopRatedAttractionDTO {
    private UUID attractionId;
    private String name;
    private double averageRating;
}