package id.ac.ui.cs.advprog.papikos.Rental.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phoneNumber;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<Rental> rentals;

    public Tenant() {}

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phone
