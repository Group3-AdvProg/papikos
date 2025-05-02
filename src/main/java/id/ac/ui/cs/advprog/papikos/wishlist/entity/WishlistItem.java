package id.ac.ui.cs.advprog.papikos.wishlist.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenantId;
    private String roomType;

    // You could use @ManyToOne for a real Tenant entity
    // @ManyToOne
    // private Tenant tenant;
}
