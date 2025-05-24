package id.ac.ui.cs.advprog.papikos.paymentMain.payload.request;

import lombok.Data;

@Data
public class TopUpRequest {
    private Long userId;
    private double amount;
    private String method; // "bank" or "virtual"
    private String password; // <- add this field
}
