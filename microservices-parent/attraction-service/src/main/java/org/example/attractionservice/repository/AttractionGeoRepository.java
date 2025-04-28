package org.example.attractionservice.repository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.attractionservice.mapper.entity.AttractionDocument;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttractionGeoRepository extends MongoRepository<AttractionDocument, UUID> {
    List<AttractionDocument> findByLocationNear(Point location, Distance distance);
}
