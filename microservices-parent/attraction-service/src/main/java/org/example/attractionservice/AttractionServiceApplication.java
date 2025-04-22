package org.example.attractionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class AttractionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttractionServiceApplication.class, args);
    }

}
