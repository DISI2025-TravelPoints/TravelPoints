package org.example.attractionservice.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.attractionservice.AttractionServiceApplication;
import org.example.attractionservice.mapper.dto.AttractionRequest;
import org.example.attractionservice.mapper.entity.Attraction;
import org.example.attractionservice.repository.AttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttractionService {

    private final AttractionRepository attractionRepository;


    private final S3Service s3Service;


    public UUID createAttraction(MultipartFile file, AttractionRequest request) {
        String path = "travelpoints_audio_files/";
        String filename = path + file.getOriginalFilename();
        s3Service.uploadFile(file);

        Attraction attraction = Attraction.builder()
                .description(request.getDescription())
                .entryFee(request.getEntryFee())
                .audioFilePath(filename)
                .geoCode(request.getGeoCode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        attractionRepository.save(attraction);
        return attraction.getId();
    }
        public void updateAttraction(UUID id, MultipartFile file, AttractionRequest request) {
            Attraction attraction = attractionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Attraction not found"));
            if (file != null && !file.isEmpty()) {
                String path = "travelpoints_audio_files/";
                String newFilename = path + file.getOriginalFilename();
                if (newFilename.equals(attraction.getAudioFilePath())) {
                    throw new RuntimeException("The uploaded file is identical to the existing one. Update skipped.");
                }
                if (attraction.getAudioFilePath() != null) {
                    s3Service.deleteFile(attraction.getAudioFilePath());
                }
                s3Service.uploadFile(file);
                attraction.setAudioFilePath(newFilename);
            }
            attraction.setDescription(request.getDescription());
            attraction.setEntryFee(request.getEntryFee());
            attraction.setGeoCode(request.getGeoCode());
            attraction.setLatitude(request.getLatitude());
            attraction.setLongitude(request.getLongitude());
            attraction.setLastUpdate(Date.valueOf(LocalDate.now()));
            attractionRepository.save(attraction);
        }



        public void deleteAttraction(UUID id) {
            Attraction attraction = attractionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Attraction not found"));
            if (attraction.getAudioFilePath() != null && !attraction.getAudioFilePath().isEmpty()) {
                s3Service.deleteFile(attraction.getAudioFilePath());
            }
            attractionRepository.delete(attraction);
        }




}
