package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository repo;

    @Autowired
    public TenantServiceImpl(TenantRepository repo) {
        this.repo = repo;
    }

    @Override
    public Tenant createTenant(Tenant tenant) {
        return repo.save(tenant);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return repo.findAll();
    }

    @Override
    public Optional<Tenant> getTenantById(UUID id) {
        return repo.findById(id);
    }

    @Override
    public Tenant updateTenant(UUID id, Tenant details) {
        return repo.findById(id)
                .map(t -> {
                    t.setFullName(details.getFullName());
                    t.setPhoneNumber(details.getPhoneNumber());
                    return repo.save(t);
                })
                .orElseThrow(() -> new RuntimeException("Tenant not found: " + id));
    }

    @Override
    public void deleteTenant(UUID id) {
        repo.deleteById(id);
    }
}
