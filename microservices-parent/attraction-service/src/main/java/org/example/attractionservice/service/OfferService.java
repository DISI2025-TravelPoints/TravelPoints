package org.example.attractionservice.service;
import lombok.RequiredArgsConstructor;
import org.example.attractionservice.mapper.entity.Offer;
import org.example.attractionservice.repository.OfferRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendOfferToUsers(Offer offerTemplate, List<Long> userIds) {

        userIds.forEach(userId -> {
            Offer offer = Offer.builder()
                    .title(offerTemplate.getTitle())
                    .description(offerTemplate.getDescription())
                    .validUntil(offerTemplate.getValidUntil())
                    .attractionId(offerTemplate.getAttractionId())
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build();

            offerRepository.save(offer);

            System.out.printf("Saved offer for userId: %d%n",userId);

            messagingTemplate.convertAndSend("/topic/offers/" + userId, offer);
            System.out.printf("Sent WebSocket message to /topic/offers/%d%n", userId);
        });
    }

    public List<Offer> getOffersForUser(Long userId) {
        List<Offer> offers = offerRepository.findByUserId(userId);
        return offers;
    }
}





