package id.ac.ui.cs.advprog.papikos.Payment;

public class BankTransferPayment implements PaymentStrategy {

    @Override
    public boolean pay(double amount) {
        System.out.println("Paying " + amount + " via Bank Transfer.");
        return true; // Simulated success
    }
}
