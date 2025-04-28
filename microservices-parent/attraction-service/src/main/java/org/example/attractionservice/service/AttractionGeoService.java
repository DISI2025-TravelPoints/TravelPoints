package org.example.attractionservice.service;

import lombok.AllArgsConstructor;
import org.example.attractionservice.mapper.dto.AttractionPostRequest;
import org.example.attractionservice.mapper.entity.AttractionDocument;
import org.example.attractionservice.repository.AttractionGeoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Distance;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AttractionGeoService {
    private final AttractionGeoRepository attractionGeoRepository;


    public void saveLocation(UUID attractionId, Double latitude, Double longitude) {
        GeoJsonPoint location = new GeoJsonPoint(longitude, latitude);
        attractionGeoRepository.save(new AttractionDocument(attractionId, location));
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

    public List<AttractionDocument> findNearbyAttractions(double latitude, double longitude, double radiusKm) {
        Point point = new Point(longitude, latitude);
        Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);
        return attractionGeoRepository.findByLocationNear(point, distance);
    }
}
