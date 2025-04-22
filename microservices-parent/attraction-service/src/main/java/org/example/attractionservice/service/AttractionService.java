package org.example.attractionservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.attractionservice.mapper.dto.AttractionRequest;
import org.example.attractionservice.mapper.entity.Attraction;
import org.example.attractionservice.repository.AttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AttractionService {
    @Autowired
    private final AttractionRepository attractionRepository;

    public UUID createAttraction(AttractionRequest attractionRequest, String filename) {
        Attraction attraction = Attraction.builder()
                .name(attractionRequest.getName())
                .description(attractionRequest.getDescription())
                .entryFee(attractionRequest.getEntryFee())
                .audioFilePath(filename)
                .lastUpdate(new Date(System.currentTimeMillis()))
                .build();
        return attractionRepository.save(attraction).getId();
    }
}
