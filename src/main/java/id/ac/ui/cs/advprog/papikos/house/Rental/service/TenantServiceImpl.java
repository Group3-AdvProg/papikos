package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.TenantRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository repository;

    public TenantServiceImpl(TenantRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tenant createTenant(Tenant tenant) {
        return repository.save(tenant);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return repository.findAll();
    }

    @Override
    public Tenant getTenantById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Tenant updateTenant(Long id, Tenant tenant) {
        Tenant existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setFullName(tenant.getFullName());
            existing.setPhoneNumber(tenant.getPhoneNumber());
            return repository.save(existing);
        }
        return null;
    }

    @Override
    public void deleteTenant(Long id) {
        repository.deleteById(id);
    }
}