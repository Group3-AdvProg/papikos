package id.ac.ui.cs.advprog.papikos.chat.repository;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring-Data JPA repository for {@link ChatRoom}.
 *
 * Spring will auto-generate the SQL for the finder methods declared below.
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * Return the unique 1-to-1 room between a tenant and landlord, if it exists.
     * <p>
     * Naming convention <code>findByTenant_IdAndLandlord_Id</code> lets Spring build:
     * <pre>
     * SELECT * FROM chat_room
     *  WHERE tenant_id   = :tenantId
     *    AND landlord_id = :landlordId
     * LIMIT 1;
     * </pre>
     */
    Optional<ChatRoom> findByTenant_IdAndLandlord_Id(Long tenantId,
                                                     Long landlordId);

    /**
     * Fetch every room where the given user participates either as tenant
     * <em>or</em> landlord, returning them in the order dictated by {@link Sort}.
     */
    List<ChatRoom> findByTenant_IdOrLandlord_Id(Long tenantId,
                                                Long landlordId,
                                                Sort sort);
}
