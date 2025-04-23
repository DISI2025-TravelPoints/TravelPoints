package org.example.attractionservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.attractionservice.mapper.dto.AttractionGetRequest;
import org.example.attractionservice.mapper.dto.AttractionPostRequest;
import org.example.attractionservice.mapper.entity.AttractionDocument;
import org.example.attractionservice.service.AttractionGeoService;
import org.example.attractionservice.service.AttractionService;
import org.example.attractionservice.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attraction")
@RequiredArgsConstructor
@CrossOrigin
public class AttractionController {
    private static final Logger log = LoggerFactory.getLogger(AttractionController.class);
    private final AttractionService attractionService;
    private final S3Service s3Service; //handles audio files upload
    /*
        Handles geocode encoding of lat and long.
        It saves the data using the attraction UUID.
        Might be a wrong approach because if it crashes before it can create the JSON object,
        it ends up to only a MySql object.
        FIXME might need fixing
     */
    private final AttractionGeoService attractionGeoService;

    //OBSOLETE
    @PostMapping("/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        s3Service.uploadFile(file);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAttraction(
            @RequestPart("attraction") AttractionPostRequest attractionPostRequest,
            @RequestPart("file")MultipartFile file) {
        // upload the file and then get the url
        try {
            log.info("Creating attraction {}", attractionPostRequest);
            String filename = s3Service.uploadFile(file);
            UUID attractionId = attractionService.createAttraction(attractionPostRequest, filename);
            log.info("Saved attraction with id: {}", attractionId);
            attractionGeoService.saveLocation(attractionId, attractionPostRequest.getLatitude(), attractionPostRequest.getLongitude());
            log.info("merge loggeru 2");
            return ResponseEntity.status(HttpStatus.CREATED).body(attractionId);
        }
        catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    /*
        To retrieve all the existing location we need to fetch the attractions and their geolocations from MongoDB.
        After that we need to match the ids and set the coordinates for the request DTO.

        The map is used for faster id retrieving.
     */
    @GetMapping
    public ResponseEntity<?> getAllAttractions() {
        List<AttractionGetRequest> attractions = attractionService.getAllAttractions();
        List<AttractionDocument> geoLocations = attractionGeoService.getAllLocations();
        Map<UUID, AttractionDocument> geoLocationMap = geoLocations.stream()
                .collect(Collectors.toMap(AttractionDocument::getId, geoLocation -> geoLocation));
        attractions.forEach(attraction -> {
            AttractionDocument location = geoLocationMap.get(attraction.getId());

            if(location != null) {
                attraction.setLongitude(location.getLocation().getCoordinates().get(0));
                attraction.setLatitude(location.getLocation().getCoordinates().get(1));
            }
        });
        return ResponseEntity.ok(attractions);
    }

    /*
      Retrieves an attraction using the id
     */
    @GetMapping("/{attractionId}")
    public ResponseEntity<?> getAttractionById(@PathVariable("attractionId") UUID attractionId) {
        AttractionGetRequest attraction = attractionService.getAttractionById(attractionId);
        if (attraction != null) {
            AttractionDocument geoLocation = attractionGeoService.getLocationById(attractionId);
            if(geoLocation != null) {
                attraction.setLongitude(geoLocation.getLocation().getCoordinates().get(0));
                attraction.setLatitude(geoLocation.getLocation().getCoordinates().get(1));
                return ResponseEntity.ok(attraction);
            }
            else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
