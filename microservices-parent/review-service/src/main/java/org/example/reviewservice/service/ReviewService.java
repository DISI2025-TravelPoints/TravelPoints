package org.example.reviewservice.service;

import lombok.RequiredArgsConstructor;
import org.example.reviewservice.mapper.dto.LoggedInUserDTO;
import org.example.reviewservice.mapper.dto.ReviewPostDTO;
import org.example.reviewservice.mapper.dto.ReviewResponseDTO;
import org.example.reviewservice.mapper.dto.ReviewAttractionStatsDTO;
import org.example.reviewservice.mapper.dto.ReviewUpdateDTO;
import org.example.reviewservice.mapper.entity.Review;
import org.example.reviewservice.repository.ReviewRepository;
import org.example.reviewservice.config.JWTUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReviewService {

   private final ReviewRepository reviewRepository;

   private final RestTemplate restTemplate;

   private LoggedInUserDTO getLoggedInUser(String token) {
      String url = "http://user-service:8081/api/user/me";
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + token);
      HttpEntity<Void> request = new HttpEntity<>(headers);
      ResponseEntity<LoggedInUserDTO> response = restTemplate.exchange(
              url,
              HttpMethod.GET,
              request,
              LoggedInUserDTO.class
      );

      return response.getBody();
   }

   public boolean attractionExists(UUID attractionId) {
      String url = "http://attraction-service:8082/api/attraction/" + attractionId;
      try {
         restTemplate.getForEntity(url, String.class);
         return true;
      } catch (HttpClientErrorException.NotFound e) {
         return false;
      }
   }

   public String addReview(ReviewPostDTO dto, String jwtToken) {
      if (!attractionExists(dto.getAttractionId())) {
         throw new IllegalArgumentException("Attraction not found.");
      }
      LoggedInUserDTO user = getLoggedInUser(jwtToken);
      Review review = new Review();
      review.setAttractionId(dto.getAttractionId());
      review.setRating(dto.getRating());
      review.setComment(dto.getComment());
      review.setUserId(user.getId());
      reviewRepository.save(review);
      return "Review successfully saved.";
   }


   public List<ReviewResponseDTO> getAllReviews() {
      return reviewRepository.findAll().stream().map(review -> ReviewResponseDTO.builder()
              .id(review.getId())
              .attractionId(review.getAttractionId())
              .userId(review.getUserId())
              .rating(review.getRating())
              .comment(review.getComment())
              .createdAt(review.getCreatedAt())
              .build()
      ).toList();
   }

   public String updateReview(UUID reviewId, ReviewUpdateDTO dto, String token) {
      Review review = reviewRepository.findById(reviewId)
              .orElseThrow(() -> new RuntimeException("Review not found"));

      LoggedInUserDTO user = getLoggedInUser(token);

      if (!review.getUserId().equals(user.getId())) {
         throw new RuntimeException("You are not authorized to update this review.");
      }

      review.setRating(dto.getRating());
      review.setComment(dto.getComment());

      reviewRepository.save(review);
      return "Review successfully updated.";
   }
   public String deleteReview(UUID reviewId, String token) {
      Review review = reviewRepository.findById(reviewId)
              .orElseThrow(() -> new RuntimeException("Review not found"));

      LoggedInUserDTO user = getLoggedInUser(token);

      if (!review.getUserId().equals(user.getId())) {
         throw new RuntimeException("You are not authorized to delete this review.");
      }

      reviewRepository.deleteById(reviewId);
      return "Review successfully deleted.";
   }

   public double getAverageRatingForAttraction(UUID attractionId) {
      List<Review> reviews = reviewRepository.findAllByAttractionId(attractionId);
      if (reviews.isEmpty()) {
         throw new IllegalArgumentException("No reviews found for this attraction.");
      }
      double total = reviews.stream().mapToInt(Review::getRating).sum();
      return total / reviews.size();
   }

   public List<ReviewAttractionStatsDTO> getAttractionStats() {
      Map<UUID, Long> countMap = reviewRepository.findAll().stream()
              .collect(Collectors.groupingBy(Review::getAttractionId, Collectors.counting()));

      return countMap.entrySet().stream()
              .map(entry -> {
                 UUID attractionId = entry.getKey();
                 long count = entry.getValue();
                 String name;
                 try {
                    ResponseEntity<Map> response = restTemplate.getForEntity(
                            "http://attraction-service:8082/api/attraction/" + attractionId,
                            Map.class
                    );
                    name = (String) response.getBody().get("name");
                 } catch (Exception e) {
                    name = "Unknown (error fetching name)";
                    System.err.println("Error fetching attraction name for ID: " + attractionId + ", error: " + e.getMessage());
                 }
                 return new ReviewAttractionStatsDTO(attractionId, name, count);
              })
              .sorted((a, b) -> Long.compare(b.getReviewCount(), a.getReviewCount()))
              .toList();
   }











}
