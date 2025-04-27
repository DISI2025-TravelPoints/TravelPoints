package org.example.attractionservice.mapper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttractionPostRequest {
    private String name;
    private String description;
    private Float entryFee;
    private Double latitude;
    private Double longitude;
}
