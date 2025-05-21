package id.ac.ui.cs.advprog.papikos.house.Rental.model;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("TENANT") // optional but helps to distinguish subclass in DB
@Table(name = "tenants") // optional: give the table a clean name
public class Tenant extends User {

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
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

    public void addRental(Rental rental) {
        rentals.add(rental);
        rental.setTenant(this);
    }

    public void removeRental(Rental rental) {
        rentals.remove(rental);
        rental.setTenant(null);
    }
}
