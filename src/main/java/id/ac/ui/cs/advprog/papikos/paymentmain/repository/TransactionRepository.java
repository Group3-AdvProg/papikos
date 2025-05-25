package id.ac.ui.cs.advprog.papikos.paymentmain.repository;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.paymentmain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByUser(User user);
    List<Transaction> findByTargetUser(User user);
    List<Transaction> findByUserAndType(User user, String type);
    Page<Transaction> findByUserAndTimestampBetween(User user, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<Transaction> findByUserAndTypeAndTimestampBetween(
        User user, String type, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
