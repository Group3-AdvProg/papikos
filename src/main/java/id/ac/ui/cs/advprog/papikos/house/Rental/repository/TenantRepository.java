package id.ac.ui.cs.advprog.papikos.house.Rental.repository;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
}