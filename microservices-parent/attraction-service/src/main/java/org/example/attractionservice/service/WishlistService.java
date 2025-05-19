package org.example.attractionservice.service;
import org.example.attractionservice.mapper.entity.Wishlist;
import org.example.attractionservice.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.example.attractionservice.mapper.dto.UserDTO;
import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;

//@RequiredArgsConstructor
@Service
public class WishlistService {

   @Autowired
    private   WishlistRepository wishlistRepository;
    @Autowired
    private  RestTemplate restTemplate;

    public List<UserDTO> getUsersWhoSavedAttraction(UUID attractionId,
                                                    String authHeader) {

        List<Long> userIds = wishlistRepository.findAll().stream()
                .filter(w -> w.getAttractionId().equals(attractionId))
                .map(Wishlist::getUserId)
                .distinct()
                .toList();

        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        String url = "http://user-service:8081/api/user/names-by-ids";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);   // forward token
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<Long>> entity = new HttpEntity<>(userIds, headers);

        ResponseEntity<UserDTO[]> resp = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                UserDTO[].class
        );

        return resp.getBody() == null
                ? Collections.emptyList()
                : Arrays.asList(resp.getBody());
    }


    public void addToWishlist(Long userId, UUID attractionId) {
        if (wishlistRepository.findByUserIdAndAttractionId(userId, attractionId).isPresent()) {
            throw new IllegalArgumentException("Attraction already in wishlist.");
        }

        wishlistRepository.save(
                Wishlist.builder()
                        .userId(userId)
                        .attractionId(attractionId)
                        .build()
        );
    }

    @Transactional
    public void removeFromWishlist(Long userId, UUID attractionId) {
        wishlistRepository.deleteByUserIdAndAttractionId(userId, attractionId);
    }

    public List<UUID> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .stream()
                .map(Wishlist::getAttractionId)
                .collect(Collectors.toList());
    }
}

