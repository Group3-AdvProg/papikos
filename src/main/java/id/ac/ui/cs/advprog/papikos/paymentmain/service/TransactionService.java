package id.ac.ui.cs.advprog.papikos.paymentmain.service;

import id.ac.ui.cs.advprog.papikos.paymentmain.model.Transaction;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.rental.repository.RentalRepository;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.request.PaymentRequest;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.paymentmain.repository.TransactionRepository;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentalRepository rentalRepository;

    public Transaction recordTransaction(User user, User targetUser, double amount, String type, String method) {
        Transaction transaction = Transaction.builder()
                .user(user)
                .targetUser(targetUser)
                .amount(amount)
                .type(type)
                .method(method)
                .timestamp(LocalDateTime.now())
                .build();
        return transactionRepository.save(transaction);
    }

    public ApiResponse handleRentPayment(PaymentRequest request, String tenantEmail) {
        User tenant = userRepository.findByEmail(tenantEmail)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tenant not found"));

        User landlord = userRepository.findById(request.getTargetId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Landlord not found"));

        if (tenant.getBalance() < request.getAmount()) {
            return new ApiResponse("FAILED", "Insufficient balance.", null);
        }

        tenant.setBalance(tenant.getBalance() - request.getAmount());
        landlord.setBalance(landlord.getBalance() + request.getAmount());
        userRepository.saveAll(List.of(tenant, landlord));

        Optional<Rental> rentalOpt = rentalRepository.findTopByTenantAndHouseOwnerAndIsPaidFalseOrderByIdDesc(tenant, landlord);
        rentalOpt.ifPresent(rental -> {
            rental.setPaid(true);
            rentalRepository.save(rental);
        });

        this.recordTransaction(tenant, landlord, request.getAmount(), "RENT_PAYMENT", "wallet");

        return new ApiResponse("SUCCESS", "Rent payment successful.", "/management.html");
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

    public void createTransaction(Long userId, Double amount, String method, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        this.recordTransaction(user, null, amount, type, method);
    }

    public Page<Transaction> getTransactionsByUserTypeAndDate(
            User user, String type, LocalDateTime fromDate, LocalDateTime toDate, PageRequest pageRequest) {
        return transactionRepository.findByUserAndTypeAndTimestampBetween(user, type, fromDate, toDate, pageRequest);
    }

    public List<Transaction> getTransactionsByUserOrTarget(User user) {
        List<Transaction> asPayer = transactionRepository.findByUser(user);
        List<Transaction> asRecipient = transactionRepository.findByTargetUser(user);
        List<Transaction> all = new ArrayList<>();
        all.addAll(asPayer);
        all.addAll(asRecipient);
        all.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp())); // latest first
        return all;
    }
}
