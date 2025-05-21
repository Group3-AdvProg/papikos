package id.ac.ui.cs.advprog.papikos.wishlist.repository;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByTenantId(Long tenantId);
    List<WishlistItem> findByHouseId(Long houseId);
    WishlistItem findByTenantIdAndHouseId(Long tenantId, Long houseId);
}
