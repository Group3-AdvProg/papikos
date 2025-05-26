package id.ac.ui.cs.advprog.papikos.paymentTest.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.rental.repository.RentalRepository;
import id.ac.ui.cs.advprog.papikos.house.rental.service.RentalService;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.request.PaymentRequest;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.paymentmain.service.PaymentService;
import id.ac.ui.cs.advprog.papikos.paymentmain.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest {

    private PaymentService service;
    private UserRepository userRepository;
    private RentalRepository rentalRepository;
    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        TransactionService transactionService = Mockito.mock(TransactionService.class);
        rentalRepository = Mockito.mock(RentalRepository.class);
        rentalService = Mockito.mock(RentalService.class);

        service = new PaymentService(userRepository, transactionService, rentalRepository, rentalService);
    }

    private void mockRental() {
        Rental rental = new Rental();
        rental.setId(123L);
        Mockito.when(rentalRepository.findById(123L)).thenReturn(Optional.of(rental));
        Mockito.when(rentalRepository.save(Mockito.any())).thenReturn(rental);
        Mockito.doNothing().when(rentalService).updateRentalCache(Mockito.any());
    }

    @Test
    void shouldSucceedForBankPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100_000.0);
        request.setMethod("bank");
        request.setUserId(1L);
        request.setTargetId(2L);
        request.setRentalId(123L);

        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(200_000.0);

        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(0.0);

        mockRental();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(landlord));

        assertTrue(service.handlePayment(request));
    }

    @Test
    void shouldSucceedForVirtualAccountPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(75_000.0);
        request.setMethod("virtual");
        request.setUserId(1L);
        request.setTargetId(2L);
        request.setRentalId(123L);

        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(100_000.0);

        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(0.0);

        mockRental();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(landlord));

        assertTrue(service.handlePayment(request));
    }

    @Test
    void shouldFailWithInvalidPaymentMethod() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(50_000.0);
        request.setMethod("invalid");
        request.setUserId(1L);
        request.setTargetId(2L);
        request.setRentalId(123L);

        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(100_000.0);

        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(0.0);

        mockRental();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(landlord));

        assertFalse(service.handlePayment(request));
    }

    @Test
    void shouldFailWhenBalanceIsInsufficient() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100_000.0);
        request.setMethod("bank");
        request.setUserId(1L);
        request.setTargetId(2L);
        request.setRentalId(123L);

        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(50_000.0);

        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(0.0);

        mockRental();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(landlord));

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

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(landlord));

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

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(landlord));

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

    @Test
    void testHandlePayment_landlordNotFound_shouldThrowException() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100_000.0);
        request.setMethod("bank");
        request.setUserId(1L);
        request.setTargetId(2L);
        request.setRentalId(123L);

        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(200_000.0);

        mockRental();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            service.handlePayment(request);
        });

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("Landlord not found", thrown.getReason());
    }

    @Test
    void testHandlePayment_tenantNotFound_shouldThrowException() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100_000.0);
        request.setMethod("bank");
        request.setUserId(1L);
        request.setTargetId(2L);
        request.setRentalId(123L);

        mockRental();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            service.handlePayment(request);
        });

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("Tenant not found", thrown.getReason());
    }

    @Test
    void testHandlePayment_rentalNotFound_shouldThrowException() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100_000.0);
        request.setMethod("bank");
        request.setUserId(1L);
        request.setTargetId(2L);
        request.setRentalId(999L); // any ID that won't be found

        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(200_000.0);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tenant));
        Mockito.when(rentalRepository.findById(999L)).thenReturn(Optional.empty()); // simulate not found

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            service.handlePayment(request);
        });

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("Rental not found", thrown.getReason());
    }


    @Test
    void testHandlePayment_strategyFails_shouldReturnFalse() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100_000.0);
        request.setMethod("fail");
        request.setUserId(1L);
        request.setTargetId(2L);
        request.setRentalId(123L);

        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(200_000.0);

        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(0.0);

        mockRental();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tenant));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(landlord));

        assertFalse(service.handlePayment(request));
    }
}
