package id.ac.ui.cs.advprog.papikos.house.Rental.controller;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService service;

    @PostMapping
    public ResponseEntity<Tenant> create(@RequestBody Tenant tenant) {
        Tenant created = service.createTenant(tenant);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Tenant>> list() {
        return ResponseEntity.ok(service.getAllTenants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getById(@PathVariable Long id) { //  UUID → Long
        return service.getTenantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tenant> update(@PathVariable Long id, @RequestBody Tenant tenant) { //  UUID → Long
        return ResponseEntity.ok(service.updateTenant(id, tenant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { //  UUID → Long
        service.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}
