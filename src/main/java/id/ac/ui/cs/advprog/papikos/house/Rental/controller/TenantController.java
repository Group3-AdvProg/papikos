// TenantController.java
package id.ac.ui.cs.advprog.papikos.house.Rental.controller;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService service;

    public TenantController(TenantService service) {
        this.service = service;
    }

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
    public ResponseEntity<Tenant> getById(@PathVariable UUID id) {
        return service.getTenantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tenant> update(@PathVariable UUID id, @RequestBody Tenant tenant) {
        try {
            Tenant updated = service.updateTenant(id, tenant);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}
