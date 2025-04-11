package com.group3.papikos.payment;

public class VirtualAccountPayment implements PaymentStrategy {

    @Override
    public boolean pay(double amount) {
        System.out.println("Paying " + amount + " via Virtual Account.");
        return true; // Simulated success
    }
}
