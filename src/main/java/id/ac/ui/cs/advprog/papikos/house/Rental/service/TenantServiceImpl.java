package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository repo;

    @Override
    public Tenant createTenant(Tenant tenant) {
        return repo.save(tenant);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return repo.findAll();
    }

    @Override
    public Optional<Tenant> getTenantById(Long id) { //  UUID → Long
        return repo.findById(id);
    }

    @Override
    public Tenant updateTenant(Long id, Tenant details) { //  UUID → Long
        return repo.findById(id)
                .map(t -> {
                    t.setFullName(details.getFullName());
                    t.setPhoneNumber(details.getPhoneNumber());
                    return repo.save(t);
                })
                .orElseThrow(() -> new RuntimeException("Tenant not found: " + id));
    }

    @Override
    public void deleteTenant(Long id) { // ✅ UUID → Long
        repo.deleteById(id);
    }
}
