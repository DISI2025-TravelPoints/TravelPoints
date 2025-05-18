package org.example.attractionservice.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.example.attractionservice.mapper.dto.AttractionGetRequest;
import org.example.attractionservice.mapper.dto.AttractionPostRequest;
import org.example.attractionservice.mapper.dto.NearbyAttractionsResponse;
import org.example.attractionservice.mapper.entity.Attraction;
import org.example.attractionservice.mapper.entity.AttractionDocument;
import org.example.attractionservice.service.AttractionGeoService;
import org.example.attractionservice.service.AttractionService;
import org.example.attractionservice.service.S3Service;
import org.example.attractionservice.service.VisitFrequencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attraction")
@RequiredArgsConstructor
//@CrossOrigin
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
    private final VisitFrequencyService visitFrequencyService;

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
                attraction.setLatitude(location.getLocation().getCoordinates().get(0));
                attraction.setLongitude(location.getLocation().getCoordinates().get(1));
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

    /*
        FIXME check if the data exists in both databases and then delete it. There might have been errors
        Deletes an attraction by its id.
     */
    @DeleteMapping("/{attractionId}")
    public ResponseEntity<?> deleteAttractionById(@PathVariable("attractionId") UUID attractionId) {
        // main db
        if(attractionService.exists(attractionId)){
            // mongodb
            if(attractionGeoService.exists(attractionId)){
                // s3 bucket
                String filePath = attractionService.getAttractionById(attractionId).getAudioFilePath();
                if(s3Service.fileExists(filePath)){
                    // delete file from bucket
                    s3Service.deleteFile(filePath);
                }
                // delete location data
                attractionGeoService.deleteLocationById(attractionId);
            }
            attractionService.deleteAttractionById(attractionId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @PutMapping(path = "/{attractionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAttractionById(
            @PathVariable("attractionId") UUID attractionId,
            @RequestPart("attraction") AttractionPostRequest attractionPostRequest,
            @RequestPart("file")MultipartFile file
    ){
        Optional<Attraction> optionalAttraction = attractionService.findById(attractionId);
        if (optionalAttraction.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attraction not found.");
        }

        Attraction existingAttraction = optionalAttraction.get();

        // check if the file has the same name as the one in the bucket
        String oldFilePath = existingAttraction.getAudioFilePath();
        String fileName = file.getName();
        if(!oldFilePath.split("/")[1].equals(fileName)){
            // then set the new file
            fileName = s3Service.updateFile(oldFilePath, file); // returns the full path to the file; reused existing var
        }
        attractionService.updateAttraction(existingAttraction, attractionPostRequest, fileName);
        attractionGeoService.updateLocation(existingAttraction.getId(), attractionPostRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nearby/{geohash}")
    public List<AttractionGetRequest> getNearbyAttractions(@PathVariable String geohash){
        List<AttractionDocument> nearbyAttractions = attractionGeoService.getNearbyAttractions(geohash.substring(0, 5));

        Set<UUID> nearbyAttractionIds = nearbyAttractions.stream()
                .map(AttractionDocument::getId)
                .collect(Collectors.toSet());

        Map<UUID, AttractionDocument> geoDocMap = nearbyAttractions.stream()
                .collect(Collectors.toMap(AttractionDocument::getId, Function.identity()));

        List<AttractionGetRequest> attractions = attractionService.findAllById(nearbyAttractionIds);

        return attractions.stream()
                .map(attraction -> {
                    AttractionDocument geoDoc = geoDocMap.get(attraction.getId());
                    if (geoDoc != null) {
                        attraction.setLatitude(geoDoc.getLatitude());
                        attraction.setLongitude(geoDoc.getLongitude());
                    }

                    return attraction;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAttractions(@RequestParam("keyword") String keyword) {
        List<AttractionGetRequest> attractions = attractionService.searchAttractions(keyword);

        if (attractions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No attractions found for your search.");
        }
        // map attraction ids to geolocation ids to set the latitude and longitude
        return ResponseEntity.ok(attractionGeoService.mapLocationToAttraction(attractions));
    }


    @GetMapping("/search/location")
    public ResponseEntity<?> searchAttractionsByLocation(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude) {

        // Nu mai setezi radiusKm aici!
        List<AttractionGetRequest> nearbyAttractions = attractionGeoService.findNearbyAttractions(latitude, longitude);
        if (nearbyAttractions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No attractions found near this location.");
        }

//        NearbyAttractionsResponse response = NearbyAttractionsResponse.builder()
//                .latitude(latitude)
//                .longitude(longitude)
//                .attractions(nearbyAttractions)
//                .build();

        return ResponseEntity.ok(attractionService.mapAttractionToLocation(nearbyAttractions));
    }

    @GetMapping("/visit-freq")
    public ResponseEntity<?> getFrequencyOfVisit() {
        return ResponseEntity.ok(visitFrequencyService.getAllVisits());
    }





}
