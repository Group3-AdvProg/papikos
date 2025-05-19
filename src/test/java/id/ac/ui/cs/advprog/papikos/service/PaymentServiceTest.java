package id.ac.ui.cs.advprog.papikos.service;

import id.ac.ui.cs.advprog.papikos.model.User;
import id.ac.ui.cs.advprog.papikos.payload.request.PaymentRequest;
import id.ac.ui.cs.advprog.papikos.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest {

    private PaymentService service;
    private UserRepository userRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        transactionService = Mockito.mock(TransactionService.class);

        service = new PaymentService();
        service.setUserRepository(userRepository);
        service.setTransactionService(transactionService);
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

    @Test
    void testHandleRentPayment_insufficientBalance_shouldReturnInsufficient() {
        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(50.0);

        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(0.0);

        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(java.util.Optional.of(landlord));

        String result = service.handleRentPayment("1", "2", 100.0);

        assertEquals("INSUFFICIENT", result);
    }

    @Test
    void testHandleRentPayment_sufficientBalance_shouldReturnSuccess() {
        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(200.0);

        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(0.0);

        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(java.util.Optional.of(landlord));

        String result = service.handleRentPayment("1", "2", 100.0);

        assertEquals("SUCCESS", result);
        assertEquals(100.0, tenant.getBalance());
        assertEquals(100.0, landlord.getBalance());
    }
}
