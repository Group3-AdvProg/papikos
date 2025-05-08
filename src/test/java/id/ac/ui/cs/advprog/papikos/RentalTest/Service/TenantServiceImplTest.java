package id.ac.ui.cs.advprog.papikos.RentalTest.Service;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import id.ac.ui.cs.advprog.papikos.house.Rental.repository.TenantRepository;
import id.ac.ui.cs.advprog.papikos.house.Rental.service.TenantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantServiceImplTest {

    @Mock private TenantRepository repo;
    @InjectMocks private TenantServiceImpl service;
    private UUID id = UUID.randomUUID();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Tenant gen() {
        Tenant t = new Tenant("N","P");
        t.setId(id);
        return t;
    }

    @Test
    void testCreate() {
        Tenant t = gen();
        when(repo.save(t)).thenReturn(t);
        Tenant out = service.createTenant(t);
        assertEquals(t, out);
    }

    @Test
    void testGetAll() {
        Tenant t = gen();
        when(repo.findAll()).thenReturn(Arrays.asList(t));
        assertEquals(1, service.getAllTenants().size());
    }

    @Test
    void testGetById() {
        Tenant t = gen();
        when(repo.findById(id)).thenReturn(Optional.of(t));
        Optional<Tenant> opt = service.getTenantById(id);
        assertTrue(opt.isPresent());
        assertEquals("N", opt.get().getFullName());
    }

    @Test
    void testUpdate() {
        Tenant old = gen();
        Tenant upd = new Tenant("X","Y");
        when(repo.findById(id)).thenReturn(Optional.of(old));
        when(repo.save(any())).thenAnswer(i -> {
            Tenant arg = i.getArgument(0);
            return arg;
        });
        Tenant out = service.updateTenant(id, upd);
        assertEquals("X", out.getFullName());
    }

    @Test
    void testDelete() {
        doNothing().when(repo).deleteById(id);
        assertDoesNotThrow(() -> service.deleteTenant(id));
        verify(repo).deleteById(id);
    }
}
