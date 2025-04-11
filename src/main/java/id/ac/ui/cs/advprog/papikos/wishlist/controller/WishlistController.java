package id.ac.ui.cs.advprog.papikos.wishlist.controller;

import id.ac.ui.cs.advprog.papikos.wishlist.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping("/registerTenant")
    public String registerTenant(@RequestParam String id, @RequestParam String name) {
        wishlistService.registerTenant(id, name);
        return "Tenant registered!";
    }

    @PostMapping("/add")
    public String addToWishlist(@RequestParam String tenantId, @RequestParam String itemId) {
        wishlistService.addToWishlist(tenantId, itemId);
        return "Item added!";
    }

    @PostMapping("/notifyAvailability")
    public String notifyAvailability(@RequestParam String propertyId) {
        wishlistService.notifyAvailability(propertyId);
        return "Notified relevant tenants.";
    }

}
