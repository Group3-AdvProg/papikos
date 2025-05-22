package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantService {
    Tenant createTenant(Tenant tenant);
    List<Tenant> getAllTenants();
    Optional<Tenant> getTenantById(Long id);          //  Ganti UUID -> Long
    Tenant updateTenant(Long id, Tenant tenant);      //  Ganti UUID -> Long
    void deleteTenant(Long id);                       //  Ganti UUID -> Long
}
