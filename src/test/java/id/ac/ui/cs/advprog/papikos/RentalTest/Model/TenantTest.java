package id.ac.ui.cs.advprog.papikos.RentalTest.Model;

import id.ac.ui.cs.advprog.papikos.Rental.model.Tenant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TenantTest {

    @Test
    void testTenantFields() {
        Tenant tenant = new Tenant();
        tenant.setFullName("Budi Santoso");
        tenant.setPhoneNumber("08123456789");

        assertEquals("Budi Santoso", tenant.getFullName());
        assertEquals("08123456789", tenant.getPhoneNumber());
    }
}
