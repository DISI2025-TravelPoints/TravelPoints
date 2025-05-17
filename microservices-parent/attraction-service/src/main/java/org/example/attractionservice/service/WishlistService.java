package org.example.attractionservice.service;
import org.example.attractionservice.mapper.entity.Wishlist;
import org.example.attractionservice.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

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

