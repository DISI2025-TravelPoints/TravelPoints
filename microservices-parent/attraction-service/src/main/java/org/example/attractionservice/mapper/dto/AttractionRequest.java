package org.example.attractionservice.mapper.dto;

import lombok.*;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttractionRequest {

    private String description;
    private Float entryFee;
    private String audioFilePath;
    private String geoCode;
    private Float latitude;
    private Float longitude;
}
