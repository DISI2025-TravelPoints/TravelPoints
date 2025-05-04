package org.example.attractionservice.mapper.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NearbyAttractionsResponse {
    private double latitude;
    private double longitude;
    private List<AttractionGetRequest> attractions;
}
