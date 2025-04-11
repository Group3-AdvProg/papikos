package id.ac.ui.cs.advprog.papikos.RentalTest.Service;

import id.ac.ui.cs.advprog.papikos.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.Rental.repository.TenantRepository;
import id.ac.ui.cs.advprog.papikos.Rental.service.TenantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TenantServiceImplTest {

    private TenantRepository repository;
    private TenantServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(TenantRepository.class);
        service = new TenantServiceImpl(repository);
    }

    @Test
    void testCreateTenant() {
        Tenant tenant = new Tenant();
        when(repository.save(tenant)).thenReturn(tenant);
        Tenant result = service.createTenant(tenant);
        assertEquals(tenant, result);
    }

    @Test
    void testGetAllTenants() {
        when(repository.findAll()).thenReturn(List.of(new Tenant(), new Tenant()));
        List<Tenant> result = service.getAllTenants();
        assertEquals(2, result.size());
    }

    @Test
    void testGetTenantByIdExists() {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(tenant));

        Tenant result = service.getTenantById(1L);
        assertNotNull(result);
        assertEquals(tenant, result);
    }

    @Test
    void testGetTenantByIdNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        Tenant result = service.getTenantById(99L);
        assertNull(result);
    }

    @Test
    void testUpdateTenantSuccess() {
        Long id = 1L;
        Tenant existing = new Tenant();
        existing.setId(id);
        existing.setFullName("Old Name");
        existing.setPhoneNumber("000");

        Tenant updated = new Tenant();
        updated.setFullName("New Name");
        updated.setPhoneNumber("123");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(Tenant.class))).thenAnswer(inv -> inv.getArgument(0));

        Tenant result = service.updateTenant(id, updated);

        assertNotNull(result);
        assertEquals("New Name", result.getFullName());
        assertEquals("123", result.getPhoneNumber());
    }

    @Test
    void testUpdateTenantNotFound() {
        Tenant updated = new Tenant();
        when(repository.findById(42L)).thenReturn(Optional.empty());

        Tenant result = service.updateTenant(42L, updated);

        assertNull(result);
    }

    @Test
    void testDeleteTenant() {
        service.deleteTenant(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
