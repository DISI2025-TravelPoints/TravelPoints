package org.example.attractionservice.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.attractionservice.service.WishlistService;
import org.example.attractionservice.security.JwtUtil;
import org.example.attractionservice.mapper.dto.UserDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/add")
    public ResponseEntity<String> addToWishlist(@RequestHeader("Authorization") String authHeader,
                                                @RequestParam UUID attractionId) {
        Long userId = jwtUtil.extractUserId(authHeader);
        wishlistService.addToWishlist(userId, attractionId);
        return ResponseEntity.ok("Attraction added to wishlist.");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromWishlist(@RequestHeader("Authorization") String authHeader,
                                                     @RequestParam UUID attractionId) {
        Long userId = jwtUtil.extractUserId(authHeader);
        wishlistService.removeFromWishlist(userId, attractionId);
        return ResponseEntity.ok("Attraction removed from wishlist.");
    }

    @GetMapping
    public ResponseEntity<List<UUID>> getWishlist(@RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.extractUserId(authHeader);
        List<UUID> wishlist = wishlistService.getWishlist(userId);
        return ResponseEntity.ok(wishlist);
    }


    @GetMapping("/users-by-attraction/{attractionId}")
    public ResponseEntity<List<UserDTO>> getUsersByAttraction(
            @PathVariable UUID attractionId,
            @RequestHeader("Authorization") String authHeader
    ) {
        List<UserDTO> users =
                wishlistService.getUsersWhoSavedAttraction(attractionId, authHeader);
        return ResponseEntity.ok(users);
    }


}
