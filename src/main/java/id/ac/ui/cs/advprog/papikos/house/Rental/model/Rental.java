package id.ac.ui.cs.advprog.papikos.house.Rental.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String houseId;
    private String fullName;
    private String phoneNumber;
    private LocalDate checkInDate;
    private int durationInMonths;
    private boolean approved;
    private int totalPrice;
    private boolean isPaid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
}
