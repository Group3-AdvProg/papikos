package id.ac.ui.cs.advprog.papikos.paymentTest.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.paymentMain.payload.request.PaymentRequest;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.paymentMain.service.PaymentService;
import id.ac.ui.cs.advprog.papikos.paymentMain.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest {

    private PaymentService service;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        TransactionService transactionService = Mockito.mock(TransactionService.class);

        service = new PaymentService(userRepository, transactionService);
    }

    @Test
    void shouldSucceedForBankPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100_000.0);
        request.setBalance(200_000.0);
        request.setMethod("bank");
        request.setUserId(1L);      // Add this
        request.setTargetId(2L);    // Add this

        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(200_000.0);

        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(0.0);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(landlord));

        assertTrue(service.handlePayment(request));
    }

    @Test
    void shouldSucceedForVirtualAccountPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(75_000.0);
        request.setBalance(100_000.0);
        request.setMethod("virtual");
        request.setUserId(1L);      // Add this
        request.setTargetId(2L);    // Add this

        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(100_000.0);

        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(0.0);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(landlord));

        assertTrue(service.handlePayment(request));
    }

    @Test
    void shouldFailWithInvalidPaymentMethod() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(50_000.0);    // Use Double
        request.setBalance(100_000.0);  // Use Double
        request.setMethod("invalid");

        assertFalse(service.handlePayment(request));
    }

    @Test
    void shouldFailWhenBalanceIsInsufficient() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100_000.0);   // Use Double
        request.setBalance(20_000.0);   // Use Double
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

    @Test
    void testHandleRentPayment_tenantNotFound_shouldThrowException() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.handleRentPayment("1", "2", 100.0);
        });

        assertEquals("Tenant not found", ex.getMessage());
    }

    @Test
    void testHandleRentPayment_landlordNotFound_shouldThrowException() {
        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(200.0);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.handleRentPayment("1", "2", 100.0);
        });

        assertEquals("Landlord not found", thrown.getMessage());
    }



}
