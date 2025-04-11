package id.ac.ui.cs.advprog.papikos.payload.Request;

import lombok.Data;

@Data
public class PaymentRequest {
    private double amount;
    private double balance;
    private String method; //bank or virtual
}
