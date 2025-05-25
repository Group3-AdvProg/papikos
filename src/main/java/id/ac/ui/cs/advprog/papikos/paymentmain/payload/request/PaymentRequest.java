package id.ac.ui.cs.advprog.papikos.paymentmain.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long userId;
    private Long targetId;
    private Double amount;
    private String method;
    private Long rentalId;  // ✅ Added field
}
