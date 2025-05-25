package id.ac.ui.cs.advprog.papikos.wishlist.entity;

import jakarta.persistence.*;
import lombok.*;
import id.ac.ui.cs.advprog.papikos.house.model.House;

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

    private Long tenantId;
    private Long houseId;
}
