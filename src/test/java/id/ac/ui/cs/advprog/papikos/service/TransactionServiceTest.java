package id.ac.ui.cs.advprog.papikos.service;

import id.ac.ui.cs.advprog.papikos.model.Transaction;
import id.ac.ui.cs.advprog.papikos.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void recordTransaction_shouldSaveTransaction() {
        Transaction mockTransaction = Transaction.builder()
                .userId("tenant123")
                .amount(100000)
                .type("TOP_UP")
                .method("bank")
                .timestamp(LocalDateTime.now())
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        Transaction saved = transactionService.recordTransaction("tenant123", null, 100000, "TOP_UP", "bank");

        assertNotNull(saved);
        assertEquals("tenant123", saved.getUserId());
        assertEquals("TOP_UP", saved.getType());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void getTransactionsByUser_shouldReturnList() {
        Transaction t1 = Transaction.builder().userId("u1").type("TOP_UP").build();
        Transaction t2 = Transaction.builder().userId("u1").type("RENT_PAYMENT").build();

        when(transactionRepository.findByUserId("u1")).thenReturn(Arrays.asList(t1, t2));

        List<Transaction> result = transactionService.getTransactionsByUser("u1");

        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findByUserId("u1");
    }

    @Test
    void getTransactionsByUserAndType_shouldReturnFilteredList() {
        Transaction t1 = Transaction.builder().userId("u1").type("TOP_UP").build();

        when(transactionRepository.findByUserIdAndType("u1", "TOP_UP")).thenReturn(List.of(t1));

        List<Transaction> result = transactionService.getTransactionsByUserAndType("u1", "TOP_UP");

        assertEquals(1, result.size());
        assertEquals("TOP_UP", result.get(0).getType());
        verify(transactionRepository, times(1)).findByUserIdAndType("u1", "TOP_UP");
    }

    @Test
    void getTransactionsByUserAndDate_withPagination_shouldReturnPage() {
        String userId = "user123";
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 31, 23, 59);
        Pageable pageable = PageRequest.of(0, 10);

        Transaction t1 = Transaction.builder()
                .userId(userId)
                .timestamp(LocalDateTime.of(2024, 1, 5, 10, 0))
                .build();

        Transaction t2 = Transaction.builder()
                .userId(userId)
                .timestamp(LocalDateTime.of(2024, 1, 20, 15, 0))
                .build();

        Page<Transaction> mockPage = new PageImpl<>(List.of(t1, t2));

        when(transactionRepository.findByUserIdAndTimestampBetween(userId, from, to, pageable))
                .thenReturn(mockPage);

        Page<Transaction> result = transactionService.getTransactionsByUserAndDate(userId, from, to, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(t1, result.getContent().get(0));
        assertEquals(t2, result.getContent().get(1));
        verify(transactionRepository, times(1)).findByUserIdAndTimestampBetween(userId, from, to, pageable);
    }
}
