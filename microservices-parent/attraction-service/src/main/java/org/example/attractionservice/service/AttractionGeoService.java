package org.example.attractionservice.service;

import ch.hsr.geohash.GeoHash;
import com.jayway.jsonpath.Criteria;
import lombok.AllArgsConstructor;
import org.example.attractionservice.mapper.dto.AttractionGetRequest;
import org.example.attractionservice.mapper.dto.AttractionPostRequest;
import org.example.attractionservice.mapper.entity.Attraction;
import org.example.attractionservice.mapper.entity.AttractionDocument;
import org.example.attractionservice.repository.AttractionGeoRepository;
import org.example.attractionservice.repository.AttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AttractionGeoService {
    private final AttractionGeoRepository attractionGeoRepository;
    private final AttractionRepository attractionRepository;

    public void saveLocation(UUID attractionId, Double latitude, Double longitude) {
        GeoJsonPoint location = new GeoJsonPoint(longitude, latitude);
        String ghash = generateGeohash(latitude, longitude, 7);
        attractionGeoRepository.save(new AttractionDocument(attractionId, location, ghash));
    }

    public List<AttractionDocument> getAllLocations() {
        return attractionGeoRepository.findAll();
    }

    public AttractionDocument getLocationById(UUID attractionId) {
        return attractionGeoRepository.findById(attractionId).orElse(null);
    }

    public void deleteLocationById(UUID attractionId) {
        attractionGeoRepository.deleteById(attractionId);
    }

    public boolean exists(UUID attractionId) {
        return attractionGeoRepository.existsById(attractionId);
    }

    public void updateLocation(UUID id, AttractionPostRequest attractionPostRequest) {
        AttractionDocument attractionDocument = attractionGeoRepository.findById(id).orElse(null);
        if (attractionDocument != null) {
            attractionDocument.setLocation(new GeoJsonPoint(attractionPostRequest.getLatitude(), attractionPostRequest.getLongitude()));
            attractionGeoRepository.save(attractionDocument);
        }
        else{
            saveLocation(id, attractionPostRequest.getLatitude(), attractionPostRequest.getLongitude());
        }
    }

    private String generateGeohash(double latitude, double longitude, int precision) {
        GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, precision);
        return geoHash.toBase32();
    }

    public List<AttractionDocument> getNearbyAttractions(String geohashPrefix){
        return attractionGeoRepository.findByGeohashStartingWith(geohashPrefix);
    }

    public List<AttractionGetRequest> findNearbyAttractions(double latitude, double longitude) {
        double radiusKm = 10.0;

        Point point = new Point(longitude, latitude);
        Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);

        List<AttractionDocument> documents = attractionGeoRepository.findByLocationNear(point, distance);

        return documents.stream()
                .map(doc -> {
                    Optional<Attraction> optionalAttraction = attractionRepository.findById(doc.getId());
                    if (optionalAttraction.isEmpty()) return null;

                    Attraction attraction = optionalAttraction.get();

                    return AttractionGetRequest.builder()
                            .id(attraction.getId())
                            .name(attraction.getName())
                            .description(attraction.getDescription())
                            .entryFee(attraction.getEntryFee())
                            .audioFilePath(attraction.getAudioFilePath())
                            .lastUpdate(attraction.getLastUpdate())
                            .latitude(doc.getLocation().getY())
                            .longitude(doc.getLocation().getX())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

}
