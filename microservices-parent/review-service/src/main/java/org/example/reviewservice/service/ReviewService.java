package org.example.reviewservice.service;

import lombok.RequiredArgsConstructor;
import org.example.reviewservice.mapper.dto.ReviewPostDTO;
import org.example.reviewservice.mapper.entity.Review;
import org.example.reviewservice.repository.ReviewRepository;
import org.example.reviewservice.config.JWTUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

   private ReviewRepository reviewRepository;



}
