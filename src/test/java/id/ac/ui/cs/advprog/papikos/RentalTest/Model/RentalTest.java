package id.ac.ui.cs.advprog.papikos.RentalTest.Model;

import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RentalTest {

    @Test
    void testGettersAndSetters() {
        Long id = 123L;

        House house = new House();
        house.setId(10L);
        house.setName("Kos Elite");

        User tenant = new User();
        tenant.setId(99L);
        tenant.setFullName("Darren Tenant");
        tenant.setPhoneNumber("08123456789");
        tenant.setRole("ROLE_TENANT");

        Rental r = new Rental();
        r.setId(id);
        r.setHouse(house);
        r.setTenant(tenant);
        r.setFullName("Darren");
        r.setPhoneNumber("08123");
        r.setCheckInDate(LocalDate.of(2025, 5, 10));
        r.setDurationInMonths(3);
        r.setApproved(true);
        r.setTotalPrice(1500000);
        r.setPaid(true);

        assertEquals(id, r.getId());
        assertEquals(house, r.getHouse());
        assertEquals(10L, r.getHouse().getId());
        assertEquals("Darren", r.getFullName());
        assertEquals("08123", r.getPhoneNumber());
        assertEquals(LocalDate.of(2025, 5, 10), r.getCheckInDate());
        assertEquals(3, r.getDurationInMonths());
        assertTrue(r.isApproved());
        assertEquals(1500000, r.getTotalPrice());
        assertTrue(r.isPaid());
        assertEquals(tenant, r.getTenant());
    }
}
