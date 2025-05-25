package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private static final Logger logger = LoggerFactory.getLogger(WishlistController.class);

    @PostMapping("/add")
    public ResponseEntity<Void> addToWishlist(
            @RequestParam Long userId,
            @RequestParam Long houseId
    ) {
        logger.info("User [{}] is adding House [{}] to wishlist", userId, houseId);
        wishlistService.addToWishlist(userId, houseId);
        logger.info("House [{}] successfully added to wishlist for user [{}]", houseId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromWishlist(
            @RequestParam Long userId,
            @RequestParam Long houseId
    ) {
        logger.info("User [{}] is removing House [{}] from wishlist", userId, houseId);
        wishlistService.removeFromWishlist(userId, houseId);
        logger.info("House [{}] successfully removed from wishlist for user [{}]", houseId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Long>> getWishlistByUser(
            @PathVariable Long userId
    ) {
        logger.info("Fetching wishlist for user [{}]", userId);
        return ResponseEntity.ok(wishlistService.getWishlistByTenant(userId));
    }
}