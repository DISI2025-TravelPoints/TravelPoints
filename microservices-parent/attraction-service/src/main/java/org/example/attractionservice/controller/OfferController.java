package org.example.attractionservice.controller;
import lombok.RequiredArgsConstructor;
import org.example.attractionservice.mapper.entity.Offer;
import org.example.attractionservice.service.OfferService;
import org.example.attractionservice.security.JwtUtil;
import org.example.attractionservice.mapper.dto.OfferSendRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {
    private final OfferService offerService;
    private final JwtUtil jwtUtil;

    @PostMapping("/send")
    public ResponseEntity<String> sendOffers(@RequestBody OfferSendRequest request) {
        Offer template = Offer.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .validUntil(request.getValidUntil())
                .attractionId(request.getAttractionId())
                .build();

        offerService.sendOfferToUsers(template, request.getUserIds());
        return ResponseEntity.ok("Offers sent.");
    }

    @GetMapping("/my")
    public ResponseEntity<List<Offer>> getMyOffers(@RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.extractUserId(authHeader);
        return ResponseEntity.ok(offerService.getOffersForUser(userId));
    }
}
