package org.example.attractionservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.attractionservice.service.AttractionService;
import org.example.attractionservice.service.S3Service;
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
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println(file.getName());
        String fileUrl = s3Service.uploadFile(file);
        return ResponseEntity.ok(fileUrl);
    }
}
