package org.example.attractionservice.mapper.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AttractionPostRequest {
    private String name;
    private String description;
    private Float entryFee;
    private Float latitude;
    private Float longitude;
}
