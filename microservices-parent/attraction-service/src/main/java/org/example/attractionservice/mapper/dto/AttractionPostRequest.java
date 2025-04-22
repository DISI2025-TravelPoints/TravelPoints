package org.example.attractionservice.mapper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
public class AttractionRequest {
    private String name;
    private String description;
    private Float entryFee;
    private Float latitude;
    private Float longitude;
}
