package id.ac.ui.cs.advprog.papikos.house.Rental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardingHouseDTO {
    private String id;
    private String name;
    private int monthlyPrice;
    private UUID landlordId;
}
