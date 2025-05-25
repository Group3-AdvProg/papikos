package id.ac.ui.cs.advprog.papikos.paymentmain.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankTransferPayment implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BankTransferPayment.class);

    @Override
    public boolean pay(double amount) {
        logger.info("Paying {} via Bank Transfer.", amount);
        return true; // Simulated success
    }
}
