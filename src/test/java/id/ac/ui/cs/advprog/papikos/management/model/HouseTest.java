package id.ac.ui.cs.advprog.papikos.management.model;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HouseTest {

    @Test
    void testCreateHouseSuccess() {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");

        House house = new House("Kos Harapan", "Depok", "Nyaman", 5, 1200000.0,
                "https://dummyimage.com/kos.jpg", owner);

        assertEquals("Kos Harapan", house.getName());
        assertEquals("Depok", house.getAddress());
        assertEquals("Nyaman", house.getDescription());
        assertEquals(5, house.getNumberOfRooms());
        assertEquals(1200000.0, house.getMonthlyRent());
        assertEquals(owner, house.getOwner());
    }

    @Test
    void testSettersAndGetters() {
        User owner = new User();
        owner.setId(2L);
        owner.setEmail("user2@example.com");

        House house = new House();
        house.setName("Kos A");
        house.setAddress("Jakarta");
        house.setDescription("Bersih");
        house.setNumberOfRooms(3);
        house.setMonthlyRent(950000.0);
        house.setImageUrl("https://dummyimage.com/kos-a.jpg");
        house.setOwner(owner);

        assertEquals("Kos A", house.getName());
        assertEquals("Jakarta", house.getAddress());
        assertEquals("Bersih", house.getDescription());
        assertEquals(3, house.getNumberOfRooms());
        assertEquals(950000.0, house.getMonthlyRent());
        assertEquals("https://dummyimage.com/kos-a.jpg", house.getImageUrl());
        assertEquals(owner, house.getOwner());
    }
}
