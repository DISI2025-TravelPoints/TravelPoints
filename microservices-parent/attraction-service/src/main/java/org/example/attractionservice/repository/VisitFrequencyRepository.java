package org.example.attractionservice.repository;

import org.example.attractionservice.mapper.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VisitFrequencyRepository extends JpaRepository<Visit, UUID> {
}
