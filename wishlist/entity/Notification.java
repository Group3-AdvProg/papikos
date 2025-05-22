package id.ac.ui.cs.advprog.papikos.wishlist.entity;

// ...existing imports...
import jakarta.persistence.*;
import lombok.*;
// ...existing code...

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private Long houseId;

    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
}
