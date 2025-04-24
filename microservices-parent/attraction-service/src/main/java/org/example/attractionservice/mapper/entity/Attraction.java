package org.example.attractionservice.mapper.entity;

import lombok.*;
import jakarta.persistence.*;

import java.sql.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attraction")
public class Attraction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String description;
    private Float entryFee;
    private String audioFilePath;
    private Date lastUpdate;
    private String geoCode;
    private Float latitude;
    private Float longitude;
}
