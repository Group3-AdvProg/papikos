package id.ac.ui.cs.advprog.papikos.auth.repository;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRoleAndIsApprovedFalse(String role);
}
