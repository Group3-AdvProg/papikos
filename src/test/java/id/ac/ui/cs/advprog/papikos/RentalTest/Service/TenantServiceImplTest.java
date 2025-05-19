package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantServiceImplTest {

    @Mock
    private TenantRepository repo;

    @InjectMocks
    private TenantServiceImpl service;

    private final UUID id = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Tenant baseTenant() {
        Tenant t = new Tenant();
        t.setId(id);
        t.setFullName("Alice");
        t.setPhoneNumber("081234");
        return t;
    }

    @Test
    void create_and_getAll_and_getById_and_delete() {
        Tenant t = baseTenant();

        // createTenant
        when(repo.save(t)).thenReturn(t);
        Tenant created = service.createTenant(t);
        assertSame(t, created);
        verify(repo).save(t);

        // getAllTenants
        when(repo.findAll()).thenReturn(List.of(t));
        List<Tenant> all = service.getAllTenants();
        assertEquals(1, all.size());
        assertEquals("Alice", all.get(0).getFullName());

        // getTenantById
        when(repo.findById(id)).thenReturn(Optional.of(t));
        Optional<Tenant> opt = service.getTenantById(id);
        assertTrue(opt.isPresent());
        assertEquals("081234", opt.get().getPhoneNumber());

        // deleteTenant
        doNothing().when(repo).deleteById(id);
        assertDoesNotThrow(() -> service.deleteTenant(id));
        verify(repo).deleteById(id);
    }

    @Test
    void updateTenant_successful() {
        Tenant stored = baseTenant();
        Tenant update = new Tenant();
        update.setFullName("Bob");
        update.setPhoneNumber("089999");

        when(repo.findById(id)).thenReturn(Optional.of(stored));
        when(repo.save(any(Tenant.class))).thenAnswer(inv -> inv.getArgument(0));

        Tenant result = service.updateTenant(id, update);

        assertEquals("Bob", result.getFullName());
        assertEquals("089999", result.getPhoneNumber());
        verify(repo).findById(id);
        verify(repo).save(stored);
    }

    @Test
    void updateTenant_notFound_throws() {
        when(repo.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.updateTenant(id, baseTenant());
        });
        assertTrue(ex.getMessage().contains("Tenant not found"));
        verify(repo).findById(id);
        verify(repo, never()).save(any());
    }
}
