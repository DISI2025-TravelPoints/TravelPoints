package org.example.attractionservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.attractionservice.mapper.dto.AttractionGetRequest;
import org.example.attractionservice.mapper.dto.AttractionPostRequest;
import org.example.attractionservice.mapper.entity.Attraction;
import org.example.attractionservice.repository.AttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttractionService {
    @Autowired
    private final AttractionRepository attractionRepository;

    public UUID createAttraction(AttractionPostRequest attractionPostRequest, String filename) {
        Attraction attraction = Attraction.builder()
                .name(attractionPostRequest.getName())
                .description(attractionPostRequest.getDescription())
                .entryFee(attractionPostRequest.getEntryFee())
                .audioFilePath(filename)
                .lastUpdate(new Date(System.currentTimeMillis()))
                .build();
        return attractionRepository.save(attraction).getId();
    }

    public List<AttractionGetRequest> getAllAttractions() {
        List<Attraction> attractions = attractionRepository.findAll();

        return attractions.stream().map(this::mapAttractionToRequest).collect(Collectors.toList());
    }

    private AttractionGetRequest mapAttractionToRequest(Attraction attraction){
        return AttractionGetRequest.builder()
                .id(attraction.getId())
                .audioFilePath(attraction.getAudioFilePath())
                .description(attraction.getDescription())
                .name(attraction.getName())
                .entryFee(attraction.getEntryFee())
                .lastUpdate(attraction.getLastUpdate())
                .build();
    }

    public AttractionGetRequest getAttractionById(UUID attractionId) {
        Attraction attraction = attractionRepository.findById(attractionId).orElse(null);
        if(attraction != null){
            return mapAttractionToRequest(attraction);
        }
        return null;
    }

    public Boolean deleteAttractionById(UUID attractionId) {
        if (attractionRepository.existsById(attractionId)) {
            attractionRepository.deleteById(attractionId);
            return true;
        }
        return false;
    }

    public boolean exists(UUID attractionId) {
        return attractionRepository.existsById(attractionId);
    }

    public Optional<Attraction> findById(UUID attractionId) {
        return attractionRepository.findById(attractionId);
    }

    public void updateAttraction(Attraction existingAttraction, AttractionPostRequest attractionPostRequest, String filePath) {
        existingAttraction.setName(attractionPostRequest.getName());
        existingAttraction.setDescription(attractionPostRequest.getDescription());
        existingAttraction.setEntryFee(attractionPostRequest.getEntryFee());
        existingAttraction.setLastUpdate(new Date(System.currentTimeMillis()));
        existingAttraction.setAudioFilePath(filePath);

        attractionRepository.save(existingAttraction);
    }

    public List<AttractionGetRequest> searchAttractions(String keyword) {
        List<Attraction> attractions = attractionRepository.findAll();

        // Căutare pentru cuvântul cheie (parțial) în nume sau descriere
        return attractions.stream()
                .filter(attraction -> attraction.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        attraction.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .map(this::mapAttractionToRequest)
                .collect(Collectors.toList());
    }

    public List<AttractionGetRequest> mapAttractionToLocation(List<AttractionGetRequest> geolocations){
        List<Attraction> attractions = attractionRepository.findAll();
        Map<UUID, Attraction> attractionMap = attractions.stream()
                .collect(Collectors.toMap(Attraction::getId, Function.identity()));

        return geolocations.stream()
                .peek(geolocation -> {
                    Attraction attraction = attractionMap.get(geolocation.getId());
                    if (attraction != null) {
                        geolocation.setName(attraction.getName());
                        geolocation.setDescription(attraction.getDescription());
                        geolocation.setEntryFee(attraction.getEntryFee());
                        geolocation.setLastUpdate(attraction.getLastUpdate());
                        geolocation.setAudioFilePath(attraction.getAudioFilePath());
                    }
                })
                .collect(Collectors.toList());
    }

}
