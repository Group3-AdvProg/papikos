package id.ac.ui.cs.advprog.papikos.paymentMain.model;

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

    private String userId;            // who made the payment/top-up
    private String targetUserId;      // (optional) recipient, e.g., landlord
    private double amount;
    private String type;              // e.g., "TOP_UP", "RENT_PAYMENT"
    private String method;            // e.g., "bank", "virtual"

    private LocalDateTime timestamp;
}
