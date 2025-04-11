package id.ac.ui.cs.advprog.papikos.Rental.service;

import id.ac.ui.cs.advprog.papikos.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.Rental.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    // Constructor injection for Spring and testability
    @Autowired
    public TenantServiceImpl(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    public Tenant getTenantById(Long id) {
        return tenantRepository.findById(id).orElse(null);
    }

    @Override
    public Tenant updateTenant(Long id, Tenant tenant) {
        Tenant existing = tenantRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setFullName(tenant.getFullName());
            existing.setPhoneNumber(tenant.getPhoneNumber());
            return tenantRepository.save(existing);
        }
        return null;
    }

    @Override
    public void deleteTenant(Long id) {
        tenantRepository.deleteById(id);
    }
}
