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
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        when(repository.save(tenant)).thenReturn(tenant);
        assertEquals(tenant, service.updateTenant(1L, tenant));
    }

    @Test
    void testDelete() {
        service.deleteTenant(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
