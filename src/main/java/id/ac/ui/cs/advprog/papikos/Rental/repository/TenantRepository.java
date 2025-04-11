package id.ac.ui.cs.advprog.papikos.Rental.repository;

import id.ac.ui.cs.advprog.papikos.Rental.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
}
