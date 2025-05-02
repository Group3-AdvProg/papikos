package id.ac.ui.cs.advprog.papikos.house.rental.repository;

import id.ac.ui.cs.advprog.papikos.house.rental.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
}
