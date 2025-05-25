package id.ac.ui.cs.advprog.papikos.paymentmain.payload.request;

import lombok.Data;

@Data
public class TopUpRequest {
    private double amount;
    private String method; // "bank" or "virtual"
}
