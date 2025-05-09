package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantService {
    Tenant createTenant(Tenant tenant);
    List<Tenant> getAllTenants();
    Optional<Tenant> getTenantById(UUID id);
    Tenant updateTenant(UUID id, Tenant tenant);
    void deleteTenant(UUID id);
}
