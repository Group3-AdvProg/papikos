package id.ac.ui.cs.advprog.papikos.house.Rental.repository;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    //  pakai Long, karena Tenant.java juga pakai Long sebagai ID
}
