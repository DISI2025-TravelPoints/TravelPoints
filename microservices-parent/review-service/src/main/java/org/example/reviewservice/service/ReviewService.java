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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.reviewservice.mapper.dto.TopRatedAttractionDTO;



import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;


@Service
@RequiredArgsConstructor
public class ReviewService {

   private final ReviewRepository reviewRepository;

   private final RestTemplate restTemplate;

   private final StringRedisTemplate redisTemplate;


   // Cheie pentru leaderboard
   private static final String REDIS_LEADERBOARD_KEY = "top_attractions";

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
      updateLeaderboardInRedis(dto.getAttractionId());
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
      updateLeaderboardInRedis(review.getAttractionId());

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
      updateLeaderboardInRedis(review.getAttractionId());

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


   private void updateLeaderboardInRedis(UUID attractionId) {
      double avgRating = getAverageRatingForAttraction(attractionId);
      redisTemplate.opsForZSet().add(REDIS_LEADERBOARD_KEY, attractionId.toString(), avgRating);
   }

   public List<TopRatedAttractionDTO> getTopRatedAttractionsFromRedis() {
      // 1. Preluăm din Redis
      Set<ZSetOperations.TypedTuple<String>> redisSet = redisTemplate.opsForZSet()
              .reverseRangeWithScores(REDIS_LEADERBOARD_KEY, 0, -1);

      Map<UUID, Double> leaderboard = new HashMap<>();

      if (redisSet != null) {
         for (ZSetOperations.TypedTuple<String> tuple : redisSet) {
            if (tuple.getValue() != null && tuple.getScore() != null) {
               leaderboard.put(UUID.fromString(tuple.getValue()), tuple.getScore());
            }
         }
      }

      // 2. Preluăm toate atracțiile cu review-uri din DB
      List<Review> allReviews = reviewRepository.findAll();

      Map<UUID, List<Review>> grouped = allReviews.stream()
              .collect(Collectors.groupingBy(Review::getAttractionId));

      // 3. Calculăm media din DB și adăugăm doar atracțiile care NU sunt deja în Redis
      for (Map.Entry<UUID, List<Review>> entry : grouped.entrySet()) {
         UUID attractionId = entry.getKey();
         if (!leaderboard.containsKey(attractionId)) {
            double avg = entry.getValue().stream().mapToInt(Review::getRating).average().orElse(0.0);
            leaderboard.put(attractionId, avg);
         }
      }

      // 4. Sortăm toate atracțiile după rating
      return leaderboard.entrySet().stream()
              .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
              .limit(10)
              .map(entry -> {
                 UUID id = entry.getKey();
                 double score = entry.getValue();
                 String name = "Unknown";
                 try {
                    ResponseEntity<Map> response = restTemplate.getForEntity(
                            "http://attraction-service:8082/api/attraction/" + id, Map.class);
                    name = (String) response.getBody().get("name");
                 } catch (Exception e) {
                    System.err.println("Could not fetch name for attraction: " + id);
                 }
                 return new TopRatedAttractionDTO(id, name, score);
              })
              .collect(Collectors.toList());
   }





}



