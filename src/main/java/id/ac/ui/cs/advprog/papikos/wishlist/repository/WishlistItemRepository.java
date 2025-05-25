package id.ac.ui.cs.advprog.papikos.wishlist.repository;

import id.ac.ui.cs.advprog.papikos.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    /** only returns a boolean, not the full entity */
    boolean existsByTenantIdAndHouseId(Long tenantId, Long houseId);

    /** deletes in one SQL round-trip without loading the entity */
    @Modifying
    @Transactional
    @Query("delete from WishlistItem w where w.tenantId = :tenantId and w.houseId = :houseId")
    void deleteByTenantIdAndHouseId(@Param("tenantId") Long tenantId,
                                    @Param("houseId") Long houseId);

    /** only selects the houseId column, not the whole row */
    @Query("select w.houseId from WishlistItem w where w.tenantId = :tenantId")
    List<Long> findHouseIdsByTenantId(@Param("tenantId") Long tenantId);

    /**
     * Fetch distinct tenant IDs that have this house in their wishlist,
     * so you don’t need to dedupe in Java.
     */
    @Query("select distinct w.tenantId from WishlistItem w where w.houseId = :houseId")
    List<Long> findDistinctTenantIdsByHouseId(@Param("houseId") Long houseId);
}
