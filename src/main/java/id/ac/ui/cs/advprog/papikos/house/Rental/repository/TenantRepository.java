package id.ac.ui.cs.advprog.papikos.house.Rental.repository;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    // juga pakai UUID
}
