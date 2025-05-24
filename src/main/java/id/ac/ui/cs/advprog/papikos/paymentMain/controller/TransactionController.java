package id.ac.ui.cs.advprog.papikos.paymentMain.controller;

import id.ac.ui.cs.advprog.papikos.paymentMain.model.Transaction;
import id.ac.ui.cs.advprog.papikos.paymentMain.service.TransactionService;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/{id}")
    public List<Transaction> getTransactionsByUser(@PathVariable Long id) {
        System.out.println("TransactionController: Called for user " + id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return transactionService.getTransactionsByUser(user);
    }

    @GetMapping("/type")
    public List<Transaction> getTransactionsByType(
            @RequestParam Long userId,
            @RequestParam String type
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return transactionService.getTransactionsByUserAndType(user, type);
    }

    @GetMapping("/date")
    public List<Transaction> getTransactionsByDate(
            @RequestParam Long userId,
            @RequestParam String from,
            @RequestParam String to
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime fromDate = LocalDateTime.parse(from, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime toDate = LocalDateTime.parse(to, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        // You can adjust the page size as needed
        return transactionService.getTransactionsByUserAndDate(user, fromDate, toDate, PageRequest.of(0, 100)).getContent();
    }

    @GetMapping("/filter")
    public List<Transaction> getTransactionsByFilter(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam String from,
            @RequestParam String to
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime fromDate = LocalDateTime.parse(from, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime toDate = LocalDateTime.parse(to, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        // You can adjust the page size as needed
        return transactionService.getTransactionsByUserTypeAndDate(user, type, fromDate, toDate, PageRequest.of(0, 100)).getContent();
    }
}