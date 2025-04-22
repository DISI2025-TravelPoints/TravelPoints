package org.example.attractionservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.attractionservice.mapper.dto.AttractionRequest;
import org.example.attractionservice.mapper.entity.Attraction;
import org.example.attractionservice.service.AttractionGeoService;
import org.example.attractionservice.service.AttractionService;
import org.example.attractionservice.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/attraction")
@RequiredArgsConstructor
@CrossOrigin
public class AttractionController {
    private static final Logger log = LoggerFactory.getLogger(AttractionController.class);
    private final AttractionService attractionService;
    private final S3Service s3Service; //handles audio files upload
    /*
        Handles geocode encoding of lat and long.
        It saves the data using the attraction UUID.
        Might be a wrong approach because if it crashes before it can create the JSON object,
        it ends up to only a MySql object.
        FIXME might need fixing
     */
    private final AttractionGeoService attractionGeoService;

    @PostMapping("/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        s3Service.uploadFile(file);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAttraction(
            @RequestPart("attraction") AttractionRequest attractionRequest,
            @RequestPart("file")MultipartFile file) {
        // upload the file and then get the url
        try {
            log.info("Creating attraction {}", attractionRequest);
            String filename = s3Service.uploadFile(file);
            UUID attractionId = attractionService.createAttraction(attractionRequest, filename);
            log.info("Saved attraction with id: {}", attractionId);
            attractionGeoService.saveLocation(attractionId, attractionRequest.getLatitude(), attractionRequest.getLongitude());
            log.info("merge loggeru 2");
            return ResponseEntity.status(HttpStatus.CREATED).body(attractionId);
        }
        catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }
}
