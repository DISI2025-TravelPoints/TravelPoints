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
import java.util.UUID;
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
}
