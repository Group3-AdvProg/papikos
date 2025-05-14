package id.ac.ui.cs.advprog.papikos.service;


import id.ac.ui.cs.advprog.papikos.payload.request.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest {

    private PaymentService service;

    @BeforeEach
    void setUp() {
        service = new PaymentService();
    }

    @Test
    void shouldSucceedForBankPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100_000);
        request.setBalance(200_000);
        request.setMethod("bank");

        assertTrue(service.handlePayment(request));
    }

    @Test
    void shouldSucceedForVirtualAccountPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(75_000);
        request.setBalance(100_000);
        request.setMethod("virtual");

        assertTrue(service.handlePayment(request));
    }

    @Test
    void shouldFailWithInvalidPaymentMethod() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(50_000);
        request.setBalance(100_000);
        request.setMethod("invalid");

        assertFalse(service.handlePayment(request));
    }

    @Test
    void shouldFailWhenBalanceIsInsufficient() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100_000);
        request.setBalance(20_000);
        request.setMethod("bank");

        assertFalse(service.handlePayment(request));
    }
}
