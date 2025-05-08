package id.ac.ui.cs.advprog.papikos.wishlist.repository;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByTenantId(String tenantId);
    List<WishlistItem> findByHouseId(Long houseId);
    WishlistItem findByTenantIdAndHouseId(String tenantId, Long houseId);
}
