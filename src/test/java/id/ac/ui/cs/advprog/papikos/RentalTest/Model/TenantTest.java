package id.ac.ui.cs.advprog.papikos.RentalTest.Model;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TenantTest {

    @Test
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        Tenant t = new Tenant();
        t.setId(id);
        t.setFullName("Darren");
        t.setPhoneNumber("08123");

        assertEquals(id, t.getId());
        assertEquals("Darren", t.getFullName());
        assertEquals("08123", t.getPhoneNumber());
    }
}
