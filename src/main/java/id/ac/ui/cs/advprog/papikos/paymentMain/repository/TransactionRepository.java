package id.ac.ui.cs.advprog.papikos.paymentMain.repository;

import id.ac.ui.cs.advprog.papikos.paymentMain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(String userId);
    List<Transaction> findByUserIdAndType(String userId, String type);
    Page<Transaction> findByUserIdAndTimestampBetween(String userId, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
