package id.ac.ui.cs.advprog.papikos.house.Rental.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String fullName;
    private String phoneNumber;

    @OneToMany(
            mappedBy = "tenant",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Rental> rentals = new ArrayList<>();

    public Tenant(String fullName, String phoneNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // Optional helpers
    public void addRental(Rental rental) {
        rentals.add(rental);
        rental.setTenant(this);
    }

    public void removeRental(Rental rental) {
        rentals.remove(rental);
        rental.setTenant(null);
    }
}
