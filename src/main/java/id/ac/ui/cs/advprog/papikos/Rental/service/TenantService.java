package id.ac.ui.cs.advprog.papikos.house.rental.service;

import id.ac.ui.cs.advprog.papikos.house.rental.model.Tenant;
import java.util.List;

public interface TenantService {
    Tenant createTenant(Tenant tenant);
    List<Tenant> getAllTenants();
    Tenant getTenantById(Long id);
    Tenant updateTenant(Long id, Tenant tenant);
    void deleteTenant(Long id);
}
