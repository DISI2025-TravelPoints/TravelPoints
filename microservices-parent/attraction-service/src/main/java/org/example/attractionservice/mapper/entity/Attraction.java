package org.example.attractionservice.mapper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.sql.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="attraction")
public class Attraction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private String description;
    private Float entryFee;
    private String audioFilePath;
    private Date lastUpdate;
    private String geoCode;
    private Float latitude;
    private Float longitude;
}
