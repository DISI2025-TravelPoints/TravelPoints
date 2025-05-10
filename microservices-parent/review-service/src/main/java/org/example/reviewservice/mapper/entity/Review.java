package org.example.reviewservice.mapper.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private Long userId;
    private UUID attractionId;

    @Min(1)
    @Max(5)
    private int rating;
    @Size(max = 30, message = "Comment must be at most 30 characters")
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();
}
