package id.ac.ui.cs.advprog.papikos.RentalTest.Model;

import id.ac.ui.cs.advprog.papikos.Rental.model.BoardingHouse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardingHouseTest {

    @Test
    void testBoardingHouseFields() {
        BoardingHouse bh = new BoardingHouse();
        bh.setName("Kos Damai");
        bh.setAddress("Jl. Bahagia No. 7");
        bh.setDescription("Kos nyaman dan aman");
        bh.setRoomCount(10);
        bh.setMonthlyPrice(1500000.0);

        assertEquals("Kos Damai", bh.getName());
        assertEquals("Jl. Bahagia No. 7", bh.getAddress());
        assertEquals("Kos nyaman dan aman", bh.getDescription());
        assertEquals(10, bh.getRoomCount());
        assertEquals(1500000.0, bh.getMonthlyPrice());
    }
}

