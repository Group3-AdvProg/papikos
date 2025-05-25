package id.ac.ui.cs.advprog.papikos.house.Rental.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RentalDTO {
    private Long houseId;
    private Long tenantId;
    private String fullName;
    private String phoneNumber;
    private LocalDate checkInDate;
    private int durationInMonths;
    private boolean approved;
    private int totalPrice;
    private boolean isPaid;
}
