package id.ac.ui.cs.advprog.papikos.auth.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users") // avoid keyword conflict
@Inheritance(strategy = InheritanceType.JOINED) // enable JOINED inheritance
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // e.g., "TENANT", "LANDLORD", "ADMIN"

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean isApproved = false;

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    @Column(nullable = false)
    private double balance = 0.0;

    public void increaseBalance(double amount) {
        this.balance += amount;
    }

    public void decreaseBalance(double amount) {
        this.balance -= amount;
    }
}
