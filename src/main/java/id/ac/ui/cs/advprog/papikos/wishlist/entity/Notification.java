package id.ac.ui.cs.advprog.papikos.wishlist.entity;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Notification extends Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tenantId;
    private Long ownerId;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
}
