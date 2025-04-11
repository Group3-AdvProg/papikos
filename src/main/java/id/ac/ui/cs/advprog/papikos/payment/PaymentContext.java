package id.ac.ui.cs.advprog.papikos.Payment;

import lombok.Setter;

@Setter
public class PaymentContext {
    private PaymentStrategy strategy;

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
