package id.ac.ui.cs.advprog.papikos.service;

import id.ac.ui.cs.advprog.papikos.model.Transaction;
import id.ac.ui.cs.advprog.papikos.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction recordTransaction(String userId, String targetUserId, double amount, String type, String method) {
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .targetUserId(targetUserId)
                .amount(amount)
                .type(type)               // e.g., "TOP_UP" or "RENT_PAYMENT"
                .method(method)           // e.g., "bank", "virtual"
                .timestamp(LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUser(String userId) {
        return transactionRepository.findByUserId(userId);
    }

    public List<Transaction> getTransactionsByUserAndType(String userId, String type) {
        return transactionRepository.findByUserIdAndType(userId, type);
    }
}
