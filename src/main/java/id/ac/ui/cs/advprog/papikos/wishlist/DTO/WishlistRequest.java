package id.ac.ui.cs.advprog.papikos.wishlist.DTO;
import id.ac.ui.cs.advprog.papikos.house.model.House;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistRequest {
    private String tenantId;
    private Long houseId;
}
