package org.example.reviewservice.mapper.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ReviewUpdateDTO {
    private int rating;
    private String comment;
}
