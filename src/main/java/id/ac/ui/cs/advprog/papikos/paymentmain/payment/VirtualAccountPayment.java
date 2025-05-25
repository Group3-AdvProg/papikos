package id.ac.ui.cs.advprog.papikos.paymentmain.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualAccountPayment implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(VirtualAccountPayment.class);

    @Override
    public boolean pay(double amount) {
        logger.info("Paying {} via Virtual Account.", amount);
        return true; // Simulated success
    }
}
