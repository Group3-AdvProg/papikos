package id.ac.ui.cs.advprog.papikos.paymentMain.service;

import id.ac.ui.cs.advprog.papikos.paymentMain.model.Transaction;
import id.ac.ui.cs.advprog.papikos.paymentMain.repository.TransactionRepository;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction recordTransaction(User user, User targetUser, double amount, String type, String method) {
        Transaction transaction = Transaction.builder()
                .user(user)                 // payer
                .targetUser(targetUser)     // recipient, can be null
                .amount(amount)
                .type(type)
                .method(method)
                .timestamp(LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUser(User user) {
        return transactionRepository.findByUser(user);
    }

    public List<Transaction> getTransactionsByUserAndType(User user, String type) {
        return transactionRepository.findByUserAndType(user, type);
    }

    public Page<Transaction> getTransactionsByUserAndDate(User user, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return transactionRepository.findByUserAndTimestampBetween(user, from, to, pageable);
    }
}
