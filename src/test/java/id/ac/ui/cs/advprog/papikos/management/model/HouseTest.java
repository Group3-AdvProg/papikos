package id.ac.ui.cs.advprog.papikos.management.model;

import id.ac.ui.cs.advprog.papikos.model.House;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HouseTest {

    @Test
    void testCreateHouseSuccess() {
        House house = new House("Kos Harapan", "Depok", "Nyaman", 5, 1200000.0);
        assertEquals("Kos Harapan", house.getName());
        assertEquals("Depok", house.getAddress());
        assertEquals("Nyaman", house.getDescription());
        assertEquals(5, house.getNumberOfRooms());
        assertEquals(1200000.0, house.getMonthlyRent());
    }

    @Test
    void testSettersAndGetters() {
        House house = new House();
        house.setName("Kos A");
        house.setAddress("Jakarta");
        house.setDescription("Bersih");
        house.setNumberOfRooms(3);
        house.setMonthlyRent(950000.0);

        assertEquals("Kos A", house.getName());
        assertEquals("Jakarta", house.getAddress());
        assertEquals("Bersih", house.getDescription());
        assertEquals(3, house.getNumberOfRooms());
        assertEquals(950000.0, house.getMonthlyRent());
    }
}
