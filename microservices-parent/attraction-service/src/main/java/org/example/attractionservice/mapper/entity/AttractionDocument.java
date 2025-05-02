package org.example.attractionservice.mapper.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection="attraction_geo")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AttractionDocument {
    @Id
    private UUID id;
    @Field("location")
    private GeoJsonPoint location;
    @Field("geohash")
    private String geohash;
}
