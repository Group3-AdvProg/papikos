package id.ac.ui.cs.advprog.papikos.RentalTest.Model;

import id.ac.ui.cs.advprog.papikos.house.Rental.model.Rental;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RentalTest {

    @Test
    void testGettersAndSetters() {
        Long id = 123L; // pakai Long, bukan UUID
        Rental r = new Rental();
        r.setId(id);
        r.setHouseId("H1");
        r.setFullName("Darren");
        r.setPhoneNumber("08123");
        r.setCheckInDate(LocalDate.of(2025, 5, 10));
        r.setDurationInMonths(3);
        r.setApproved(true);

        assertEquals(id, r.getId());
        assertEquals("H1", r.getHouseId());
        assertEquals("Darren", r.getFullName());
        assertEquals("08123", r.getPhoneNumber());
        assertEquals(LocalDate.of(2025, 5, 10), r.getCheckInDate());
        assertEquals(3, r.getDurationInMonths());
        assertTrue(r.isApproved());
    }
}
