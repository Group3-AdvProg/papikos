package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import java.util.List;

public interface TenantService {
    Tenant createTenant(Tenant tenant);
    List<Tenant> getAllTenants();
    Tenant getTenantById(Long id);
    Tenant updateTenant(Long id, Tenant tenant);
    void deleteTenant(Long id);
}