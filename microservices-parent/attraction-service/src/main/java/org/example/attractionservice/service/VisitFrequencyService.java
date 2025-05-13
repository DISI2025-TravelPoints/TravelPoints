package org.example.attractionservice.service;

import lombok.RequiredArgsConstructor;
import org.example.attractionservice.mapper.entity.Visit;
import org.example.attractionservice.repository.VisitFrequencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitFrequencyService {
    @Autowired
    private final VisitFrequencyRepository visitFrequencyRepository;

    public List<Visit> getAllVisits(){
        return visitFrequencyRepository.findAll();
    }
}
