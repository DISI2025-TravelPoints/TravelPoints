package org.example.reviewservice.repository;

import org.example.reviewservice.mapper.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID>{

    List<Review> findAllByAttractionId(UUID attractionId);
}