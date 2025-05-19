package id.ac.ui.cs.advprog.papikos.paymentMain.payload.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private double amount;
    private double balance;
    private String method; //bank or virtual
}
