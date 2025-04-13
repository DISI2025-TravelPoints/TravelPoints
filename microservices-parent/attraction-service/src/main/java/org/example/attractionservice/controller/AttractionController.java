package org.example.attractionservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.attractionservice.mapper.dto.AttractionRequest;
import org.example.attractionservice.mapper.entity.Attraction;
import org.example.attractionservice.service.AttractionService;
import org.example.attractionservice.service.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attraction")
@RequiredArgsConstructor
@CrossOrigin
public class AttractionController {
    private final AttractionService attractionService;
    private final S3Service s3Service; //handles audio files upload

    @PostMapping("/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        s3Service.uploadFile(file);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAttraction(@RequestBody AttractionRequest attractionRequest) {
        // upload the file and then get the url
        
    }
}
