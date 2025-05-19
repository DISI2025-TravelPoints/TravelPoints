package org.example.reviewservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.reviewservice.mapper.dto.ReviewPostDTO;
import org.example.reviewservice.mapper.dto.ReviewResponseDTO;
import org.example.reviewservice.mapper.dto.ReviewUpdateDTO;
import org.example.reviewservice.mapper.dto.ReviewAttractionStatsDTO;
import org.example.reviewservice.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    @GetMapping
    public String getMsg(){
        return "OK";
    }

    @PostMapping
    public ResponseEntity<String> addReview(
            @Valid @RequestBody ReviewPostDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token JWT lipsă sau invalid.");
        }

        String jwtToken = authHeader.replace("Bearer ", "");
        try {
            String result = reviewService.addReview(dto, jwtToken);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        List<ReviewResponseDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }


    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable UUID reviewId,
                                               @RequestBody ReviewUpdateDTO dto,
                                               @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String msg = reviewService.updateReview(reviewId, dto, token);
        return ResponseEntity.ok(msg);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable UUID reviewId,
                                               @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String msg = reviewService.deleteReview(reviewId, token);
        return ResponseEntity.ok(msg);
    }

    @GetMapping("/average-rating/{attractionId}")
    public ResponseEntity<Double> getAverageRating(@PathVariable UUID attractionId) {
        double average = reviewService.getAverageRatingForAttraction(attractionId);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/analytics/visits")
    public ResponseEntity<List<ReviewAttractionStatsDTO>> getAttractionStats() {
        return ResponseEntity.ok(reviewService.getAttractionStats());
    }







}
