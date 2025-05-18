package org.example.attractionservice.repository;
import org.example.attractionservice.mapper.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserId(Long userId);
    Optional<Wishlist> findByUserIdAndAttractionId(Long userId, UUID attractionId);
    void deleteByUserIdAndAttractionId(Long userId, UUID attractionId);
    List<Wishlist> findByAttractionId(UUID attractionId);
}
