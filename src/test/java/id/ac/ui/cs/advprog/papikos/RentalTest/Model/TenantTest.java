package id.ac.ui.cs.advprog.papikos.house.Rental.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TenantTest {

    @Test
    void testGettersAndSetters() {
        // existing simple getter/setter coverage
        UUID id = UUID.randomUUID();
        Tenant t = new Tenant();
        t.setId(id);
        t.setFullName("Darren");
        t.setPhoneNumber("08123");

        assertEquals(id, t.getId());
        assertEquals("Darren", t.getFullName());
        assertEquals("08123", t.getPhoneNumber());
    }

    @Test
    void testAddAndRemoveRentalHelpers() {
        // setup
        Tenant tenant = new Tenant("Alice", "08000");
        tenant.setId(UUID.randomUUID());

        Rental rental = new Rental();
        rental.setId(UUID.randomUUID());
        rental.setHouseId("H1");
        rental.setFullName("Alice");
        rental.setPhoneNumber("08000");
        rental.setCheckInDate(java.time.LocalDate.of(2025,1,1));
        rental.setDurationInMonths(2);
        rental.setApproved(false);

        // addRental should link both sides
        tenant.addRental(rental);
        assertTrue(tenant.getRentals().contains(rental), "Tenant should contain the rental");
        assertSame(tenant, rental.getTenant(), "Rental.getTenant() must point back to tenant");

        // removeRental should unlink both sides
        tenant.removeRental(rental);
        assertFalse(tenant.getRentals().contains(rental), "Tenant should no longer contain rental");
        assertNull(rental.getTenant(), "Rental.getTenant() must be null after removal");
    }
}
