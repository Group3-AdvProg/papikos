package id.ac.ui.cs.advprog.papikos.RentalTest.Model;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TenantTest {

    @Test
    void testGettersAndSetters() {
        Long id = 1L;
        Tenant t = new Tenant();
        t.setId(id);
        t.setFullName("Darren");
        t.setPhoneNumber("08123");

        assertEquals(id, t.getId());
        assertEquals("Darren", t.getFullName());
        assertEquals("08123", t.getPhoneNumber());
        assertNotNull(t.getRentals(), "Rentals list should not be null");
        assertTrue(t.getRentals().isEmpty(), "Rentals list should be initially empty");
    }

    @Test
    void testAddAndRemoveRentalHelpers() {
        Tenant tenant = new Tenant("Alice", "08000");
        tenant.setId(10L);  // pakai Long

        Rental rental = new Rental();
        rental.setId(20L);  // pakai Long
        rental.setHouseId("H1");
        rental.setFullName("Alice");
        rental.setPhoneNumber("08000");
        rental.setCheckInDate(LocalDate.of(2025, 1, 1));
        rental.setDurationInMonths(2);
        rental.setApproved(false);

        // Add rental
        tenant.addRental(rental);
        assertTrue(tenant.getRentals().contains(rental), "Tenant should contain the rental");
        assertSame(tenant, rental.getTenant(), "Rental.getTenant() must point back to tenant");

        // Remove rental
        tenant.removeRental(rental);
        assertFalse(tenant.getRentals().contains(rental), "Tenant should no longer contain rental");
        assertNull(rental.getTenant(), "Rental.getTenant() must be null after removal");
    }
}
