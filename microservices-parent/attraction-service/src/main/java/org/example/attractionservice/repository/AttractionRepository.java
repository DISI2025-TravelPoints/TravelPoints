package org.example.attractionservice.repository;

import org.example.attractionservice.mapper.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface AttractionRepository extends JpaRepository<Attraction, UUID> {
    List<Attraction> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}
