package com.group3.papikos.payment;

public class BankTransferPayment implements PaymentStrategy {

    @Override
    public boolean pay(double amount) {
        System.out.println("Paying " + amount + " via Bank Transfer.");
        return true; // Simulated success
    }
}
