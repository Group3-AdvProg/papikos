package id.ac.ui.cs.advprog.papikos.paymentmain.payment;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Setter
public class PaymentContext {
    private static final Logger logger = LoggerFactory.getLogger(PaymentContext.class);
    private PaymentStrategy strategy;

    public boolean executePayment(double amount, double balance) {
        if (balance < amount) {
            logger.warn("Insufficient balance.");
            return false;
        }

        if (strategy == null) {
            logger.warn("No payment method selected.");
            return false;
        }

        return strategy.pay(amount);
    }
}
