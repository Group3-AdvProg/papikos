package id.ac.ui.cs.advprog.papikos.paymentMain.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long userId;     // tenant ID
    private Long targetId;   // landlord ID
    private Double amount;
    private String method;
    private Double balance;    // optional if used by PaymentStrategy
}
