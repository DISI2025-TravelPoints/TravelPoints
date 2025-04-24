package org.example.attractionservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.attractionservice.mapper.dto.AttractionRequest;
import org.example.attractionservice.service.AttractionService;
import org.example.attractionservice.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/attraction")
@CrossOrigin
public class AttractionController {
    @Autowired
    private  final AttractionService attractionService;
    @Autowired
    private final S3Service s3Service; //handles audio files upload

    private final ObjectMapper objectMapper;
    public AttractionController(AttractionService attractionService, S3Service s3Service, ObjectMapper objectMapper) {
        this.attractionService = attractionService;
        this.s3Service = s3Service;
        this.objectMapper = objectMapper;
    }


    @PostMapping("/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        s3Service.uploadFile(file);
    }

    /*@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAttraction(@RequestBody AttractionRequest attractionRequest) {
        // upload the file and then get the url

    }*/
    @PostMapping
    public ResponseEntity<String> createAttraction(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") String json) {

        try {
            AttractionRequest request = objectMapper.readValue(json, AttractionRequest.class);
            UUID id = attractionService.createAttraction(file, request);
            return new ResponseEntity<>("Attraction created with ID: " + id, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("JSON parsing failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateAttraction(@PathVariable UUID id,
                                                   @RequestPart(value = "file", required = false) MultipartFile file,
                                                   @RequestPart("data") String json) {
        try {
            AttractionRequest request = objectMapper.readValue(json, AttractionRequest.class);
            attractionService.updateAttraction(id, file, request);
            return ResponseEntity.ok("Attraction updated with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttraction(@PathVariable UUID id) {
        try {
            attractionService.deleteAttraction(id);
            return ResponseEntity.ok("Attraction deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting attraction: " + e.getMessage());
        }
    }


}
