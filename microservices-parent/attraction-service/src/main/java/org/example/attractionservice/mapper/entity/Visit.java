package org.example.attractionservice.mapper.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name="visit")
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "attraction_id", referencedColumnName = "id")
    private Attraction attraction;
    @Column(name = "timestamp")
    private String timestamp;
}
