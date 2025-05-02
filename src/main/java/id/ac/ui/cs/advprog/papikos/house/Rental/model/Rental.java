package id.ac.ui.cs.advprog.papikos.house.Rental.model;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relasi ke Tenant
    @ManyToOne(optional = false)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    // relasi ke shared House
    @ManyToOne(optional = false)
    @JoinColumn(name = "house_id")
    private House house;

    private LocalDate checkInDate;
    private int durationInMonths;
    private boolean approved;
    private boolean cancelled;

    public Rental() {}

    public Rental(Tenant tenant,
                  House house,
                  LocalDate checkInDate,
                  int durationInMonths) {
        this.tenant = tenant;
        this.house = house;
        this.checkInDate = checkInDate;
        this.durationInMonths = durationInMonths;
        this.approved = false;
        this.cancelled = false;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public House getHouse() { return house; }
    public void setHouse(House house) { this.house = house; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public int getDurationInMonths() { return durationInMonths; }
    public void setDurationInMonths(int durationInMonths) { this.durationInMonths = durationInMonths; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
