package id.ac.ui.cs.advprog.papikos.paymentTest.payment;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentStrategyTest {

    @Test
    void bankTransfer_shouldPaySuccessfully() {
        PaymentStrategy payment = new BankTransferPayment();
        assertTrue(payment.pay(100_000));
    }

    @Test
    void virtualAccount_shouldPaySuccessfully() {
        PaymentStrategy payment = new VirtualAccountPayment();
        assertTrue(payment.pay(100_000));
    }

    @Test
    void context_shouldUseCorrectStrategy() {
        PaymentContext context = new PaymentContext();
        context.setStrategy(new VirtualAccountPayment());

        boolean result = context.executePayment(100_000, 150_000);

        assertTrue(result); // Pass Case
    }

    @Test
    void context_shouldFailIfBalanceInsufficient() {
        PaymentContext context = new PaymentContext();
        context.setStrategy(new BankTransferPayment());

        boolean result = context.executePayment(100_000, 50_000);

        assertFalse(result); // Fail Case
    }

    @Test
    void context_shouldPrintMessageWhenStrategyIsNull() {
        PaymentContext context = new PaymentContext();

        // Capture the system output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        boolean result = context.executePayment(100_000, 150_000);
        assertFalse(result);

        assertTrue(outContent.toString().contains("No payment method selected."));

    }
}
