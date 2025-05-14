package id.ac.ui.cs.advprog.papikos.repository;

import id.ac.ui.cs.advprog.papikos.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByUserId(String userId);

    List<Transaction> findByUserIdAndType(String userId, String type);
}
