package id.ac.ui.cs.advprog.papikos.house.rental.controller;

import id.ac.ui.cs.advprog.papikos.house.rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.house.rental.service.TenantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenant")
public class TenantController {

    private final TenantService service;

    public TenantController(TenantService service) {
        this.service = service;
    }

    @PostMapping
    public Tenant create(@RequestBody Tenant tenant) {
        return service.createTenant(tenant);
    }

    @GetMapping
    public List<Tenant> findAll() {
        return service.getAllTenants();
    }

    @GetMapping("/{id}")
    public Tenant findById(@PathVariable Long id) {
        return service.getTenantById(id);
    }

    @PutMapping("/{id}")
    public Tenant update(@PathVariable Long id, @RequestBody Tenant tenant) {
        return service.updateTenant(id, tenant);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteTenant(id);
    }
}
