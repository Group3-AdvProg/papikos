package com.group3.papikos.payment;

public class PaymentContext {
    private PaymentStrategy strategy;

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean executePayment(double amount, double balance) {
        if (balance < amount) {
            System.out.println("Insufficient balance.");
            return false;
        }

        if (strategy == null) {
            System.out.println("No payment method selected.");
            return false;
        }

        return strategy.pay(amount);
    }
}
