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
    void testCreate() {
        Tenant tenant = new Tenant();
        when(repository.save(tenant)).thenReturn(tenant);
        assertEquals(tenant, service.createTenant(tenant));
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(new Tenant(), new Tenant()));
        assertEquals(2, service.getAllTenants().size());
    }

    @Test
    void testFindById() {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(tenant));
        assertEquals(tenant, service.getTenantById(1L));
    }

    @Test
    void testUpdate() {
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
    void testDelete() {
        service.deleteTenant(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
