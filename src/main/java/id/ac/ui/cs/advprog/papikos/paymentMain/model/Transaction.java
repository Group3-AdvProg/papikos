package id.ac.ui.cs.advprog.papikos.paymentMain.model;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // who made the payment/top-up

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;  // optional recipient, e.g., landlord

    private double amount;

    private String type;   // e.g., "TOP_UP", "RENT_PAYMENT"
    private String method; // e.g., "bank", "virtual"
    private LocalDateTime timestamp;
}
