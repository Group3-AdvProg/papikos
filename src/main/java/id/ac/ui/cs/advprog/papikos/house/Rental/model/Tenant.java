package id.ac.ui.cs.advprog.papikos.house.Rental.model;

import jakarta.persistence.*;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

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

    public Tenant() {}

    public Tenant(String fullName, String phoneNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // --- Getters & Setters ---
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Rental> getRentals() {
        return rentals;
    }
    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
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
