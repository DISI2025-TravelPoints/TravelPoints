package org.example.reviewservice.mapper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ReviewAttractionStatsDTO {
    private UUID attractionId;
    private String name;
    private long reviewCount;
}
