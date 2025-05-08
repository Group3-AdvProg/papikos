package id.ac.ui.cs.advprog.papikos.house.Rental.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String houseId;        // ID kos yang disewa
    private LocalDate checkInDate;
    private int durationInMonths;
    private boolean approved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    public Rental() {}

    //–– Optional convenience constructor
    public Rental(String houseId, LocalDate checkInDate, int durationInMonths, Tenant tenant) {
        this.houseId = houseId;
        this.checkInDate = checkInDate;
        this.durationInMonths = durationInMonths;
        this.tenant = tenant;
        this.approved = false;
    }

    //–– Getters & Setters
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public String getHouseId() {
        return houseId;
    }
    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }
    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public int getDurationInMonths() {
        return durationInMonths;
    }
    public void setDurationInMonths(int durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Tenant getTenant() {
        return tenant;
    }
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
