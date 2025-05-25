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
    private Long id; // sesuaikan dengan entitas House
    private String name;
    private double monthlyRent; // sesuaikan dengan House
    private UUID landlordId; // bisa didapat dari house.getOwner().getId()
}