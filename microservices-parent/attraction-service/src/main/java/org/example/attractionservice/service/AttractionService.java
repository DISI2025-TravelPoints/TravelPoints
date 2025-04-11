package org.example.attractionservice.service;

import lombok.RequiredArgsConstructor;
import org.example.attractionservice.mapper.dto.AttractionRequest;
import org.example.attractionservice.repository.AttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttractionService {
    @Autowired
    private final AttractionRepository attractionRepository;

//    public UUID createAttraction(AttractionRequest attractionRequest){
//
//    }
}
