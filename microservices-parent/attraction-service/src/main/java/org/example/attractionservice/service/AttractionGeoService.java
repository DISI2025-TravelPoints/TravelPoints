package org.example.attractionservice.service;

import lombok.AllArgsConstructor;
import org.example.attractionservice.mapper.entity.AttractionDocument;
import org.example.attractionservice.repository.AttractionGeoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AttractionGeoService {
    private final AttractionGeoRepository attractionGeoRepository;


    public void saveLocation(UUID attractionId, Float latitude, Float longitude) {
        GeoJsonPoint location = new GeoJsonPoint(Double.valueOf(longitude), Double.valueOf(latitude));
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
}
