package org.example.attractionservice.mapper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttractionGetRequest {
    private UUID id;
    private String name;
    private String description;
    private Float entryFee;
    private String audioFilePath;
    private Double longitude;
    private Double latitude;
    private Date lastUpdate;
}
