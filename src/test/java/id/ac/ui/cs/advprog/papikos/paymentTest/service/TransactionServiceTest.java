package id.ac.ui.cs.advprog.papikos.paymentTest.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.rental.repository.RentalRepository;
import id.ac.ui.cs.advprog.papikos.paymentmain.model.Transaction;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.request.PaymentRequest;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.paymentmain.repository.TransactionRepository;
import id.ac.ui.cs.advprog.papikos.paymentmain.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RentalRepository rentalRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("secret");
        mockUser.setRole("TENANT");
        mockUser.setBalance(0);
    }

    @Test
    void recordTransaction_shouldSaveTransaction() {
        Transaction mockTransaction = Transaction.builder()
                .user(mockUser)
                .amount(100000)
                .type("TOP_UP")
                .method("bank")
                .timestamp(LocalDateTime.now())
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        Transaction saved = transactionService.recordTransaction(mockUser, null, 100000, "TOP_UP", "bank");

        assertNotNull(saved);
        assertEquals(mockUser, saved.getUser());
        assertEquals("TOP_UP", saved.getType());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void getTransactionsByUser_shouldReturnList() {
        Transaction t1 = Transaction.builder().user(mockUser).type("TOP_UP").build();
        Transaction t2 = Transaction.builder().user(mockUser).type("RENT_PAYMENT").build();

        when(transactionRepository.findByUser(mockUser)).thenReturn(Arrays.asList(t1, t2));

        List<Transaction> result = transactionService.getTransactionsByUser(mockUser);

        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findByUser(mockUser);
    }

    @Test
    void getTransactionsByUserAndType_shouldReturnFilteredList() {
        Transaction t1 = Transaction.builder().user(mockUser).type("TOP_UP").build();

        when(transactionRepository.findByUserAndType(mockUser, "TOP_UP")).thenReturn(List.of(t1));

        List<Transaction> result = transactionService.getTransactionsByUserAndType(mockUser, "TOP_UP");

        assertEquals(1, result.size());
        assertEquals("TOP_UP", result.get(0).getType());
        verify(transactionRepository, times(1)).findByUserAndType(mockUser, "TOP_UP");
    }

    @Test
    void getTransactionsByUserAndDate_withPagination_shouldReturnPage() {
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 31, 23, 59);
        Pageable pageable = PageRequest.of(0, 10);

        Transaction t1 = Transaction.builder()
                .user(mockUser)
                .timestamp(LocalDateTime.of(2024, 1, 5, 10, 0))
                .build();

        Transaction t2 = Transaction.builder()
                .user(mockUser)
                .timestamp(LocalDateTime.of(2024, 1, 20, 15, 0))
                .build();

        Page<Transaction> mockPage = new PageImpl<>(List.of(t1, t2));

        when(transactionRepository.findByUserAndTimestampBetween(mockUser, from, to, pageable))
                .thenReturn(mockPage);

        Page<Transaction> result = transactionService.getTransactionsByUserAndDate(mockUser, from, to, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(t1, result.getContent().get(0));
        assertEquals(t2, result.getContent().get(1));
        verify(transactionRepository, times(1)).findByUserAndTimestampBetween(mockUser, from, to, pageable);
    }

    @Test
    void createTransaction_shouldCallRecordTransactionAndSave() {
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        transactionService.createTransaction(mockUser.getId(), 50000.0, "bank", "TOP_UP");

        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void createTransaction_shouldThrowExceptionWhenUserNotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(userId, 50000.0, "bank", "TOP_UP");
        });

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getTransactionsByUserTypeAndDate_shouldReturnPage() {
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 31, 23, 59);
        PageRequest pageRequest = PageRequest.of(0, 10);

        Transaction t1 = Transaction.builder()
                .user(mockUser)
                .type("TOP_UP")
                .timestamp(LocalDateTime.of(2024, 1, 5, 10, 0))
                .build();
        Transaction t2 = Transaction.builder()
                .user(mockUser)
                .type("TOP_UP")
                .timestamp(LocalDateTime.of(2024, 1, 20, 15, 0))
                .build();

        Page<Transaction> mockPage = new PageImpl<>(List.of(t1, t2));

        when(transactionRepository.findByUserAndTypeAndTimestampBetween(mockUser, "TOP_UP", from, to, pageRequest))
                .thenReturn(mockPage);

        Page<Transaction> result = transactionService.getTransactionsByUserTypeAndDate(mockUser, "TOP_UP", from, to, pageRequest);

        assertEquals(2, result.getTotalElements());
        assertEquals(t1, result.getContent().get(0));
        assertEquals(t2, result.getContent().get(1));
        verify(transactionRepository, times(1)).findByUserAndTypeAndTimestampBetween(mockUser, "TOP_UP", from, to, pageRequest);
    }

    @Test
    void getTransactionsByUserOrTarget_shouldReturnCombinedAndSortedList() {
        Transaction asPayer = Transaction.builder()
                .user(mockUser)
                .type("TOP_UP")
                .timestamp(LocalDateTime.of(2024, 1, 10, 10, 0))
                .build();
        Transaction asRecipient = Transaction.builder()
                .targetUser(mockUser)
                .type("RENT_PAYMENT")
                .timestamp(LocalDateTime.of(2024, 1, 15, 12, 0))
                .build();

        when(transactionRepository.findByUser(mockUser)).thenReturn(List.of(asPayer));
        when(transactionRepository.findByTargetUser(mockUser)).thenReturn(List.of(asRecipient));

        List<Transaction> result = transactionService.getTransactionsByUserOrTarget(mockUser);

        assertEquals(2, result.size());
        assertEquals(asRecipient, result.get(0));
        assertEquals(asPayer, result.get(1));
        verify(transactionRepository, times(1)).findByUser(mockUser);
        verify(transactionRepository, times(1)).findByTargetUser(mockUser);
    }

    // New tests for handleRentPayment

    @Test
    void handleRentPaymentTenantNotFound_shouldThrowException() {
        PaymentRequest request = mock(PaymentRequest.class);
        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            transactionService.handleRentPayment(request, "tenant@example.com");
        });
        assertEquals("Tenant not found", ex.getReason());
    }

    @Test
    void handleRentPaymentLandlordNotFound_shouldThrowException() {
        PaymentRequest request = mock(PaymentRequest.class);
        User tenant = new User();
        tenant.setEmail("tenant@example.com");
        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(request.getTargetId()).thenReturn(2L);
        when(request.getAmount()).thenReturn(100.0);
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            transactionService.handleRentPayment(request, "tenant@example.com");
        });
        assertEquals("Landlord not found", ex.getReason());
    }

    @Test
    void handleRentPaymentInsufficientBalance_shouldReturnFailedApiResponse() {
        PaymentRequest request = mock(PaymentRequest.class);
        User tenant = new User();
        tenant.setEmail("tenant@example.com");
        tenant.setBalance(50.0);
        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(request.getTargetId()).thenReturn(2L);
        when(request.getAmount()).thenReturn(100.0);
        User landlord = new User();
        landlord.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(landlord));

        ApiResponse response = transactionService.handleRentPayment(request, "tenant@example.com");
        assertEquals("FAILED", response.getStatus());
        assertEquals("Insufficient balance.", response.getMessage());
        // no data getter available, skip data assertion
    }

    @Test
    void handleRentPaymentSuccess_noRental_shouldProcessTransaction() {
        PaymentRequest request = mock(PaymentRequest.class);
        User tenant = new User();
        tenant.setId(1L);
        tenant.setEmail("tenant@example.com");
        tenant.setBalance(200.0);
        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(100.0);

        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(request.getTargetId()).thenReturn(2L);
        when(request.getAmount()).thenReturn(150.0);
        when(userRepository.findById(2L)).thenReturn(Optional.of(landlord));
        when(rentalRepository.findTopByTenantAndHouseOwnerAndIsPaidFalseOrderByIdDesc(tenant, landlord))
                .thenReturn(Optional.empty());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse response = transactionService.handleRentPayment(request, "tenant@example.com");

        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Rent payment successful.", response.getMessage());
        // redirect path is returned in message or another field; skip direct data assertion
        assertEquals(50.0, tenant.getBalance());
        assertEquals(250.0, landlord.getBalance());
        verify(userRepository, times(1)).saveAll(anyList());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void handleRentPaymentSuccess_withRental_shouldMarkRentalPaid() {
        PaymentRequest request = mock(PaymentRequest.class);
        User tenant = new User();
        tenant.setId(1L);
        tenant.setEmail("tenant@example.com");
        tenant.setBalance(300.0);
        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(100.0);
        Rental rental = new Rental();
        rental.setPaid(false);

        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(request.getTargetId()).thenReturn(2L);
        when(request.getAmount()).thenReturn(150.0);
        when(userRepository.findById(2L)).thenReturn(Optional.of(landlord));
        when(rentalRepository.findTopByTenantAndHouseOwnerAndIsPaidFalseOrderByIdDesc(tenant, landlord))
                .thenReturn(Optional.of(rental));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse response = transactionService.handleRentPayment(request, "tenant@example.com");

        assertTrue(rental.isPaid());
        verify(rentalRepository, times(1)).save(eq(rental));
        assertEquals("SUCCESS", response.getStatus());
    }
}
