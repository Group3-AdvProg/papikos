package id.ac.ui.cs.advprog.papikos.RentalTest.Model;

import id.ac.ui.cs.advprog.papikos.Rental.model.Rental;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RentalTest {

    @Test
    void testRentalFields() {
        Rental rental = new Rental();
        rental.setCheckInDate(LocalDate.of(2025, 4, 1));
        rental.setDurationInMonths(6);

        assertEquals(LocalDate.of(2025, 4, 1), rental.getCheckInDate());
        assertEquals(6, rental.getDurationInMonths());
    }
}
