package com.group3.papikos.payload.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private double amount;
    private double balance;
    private String method; //bank or virtual
}
