package id.ac.ui.cs.advprog.papikos.management.repository;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HouseRepositoryTest {

    @Autowired
    private HouseRepository houseRepository;

    private House house;

    @BeforeEach
    void setUp() {
        house = new House("Kos Harapan", "Depok", "Bersih dan aman", 4, 1300000.0, "https://dummyimage.com/kos.jpg");
        houseRepository.save(house);
    }

    @Test
    void testSaveAndFindById() {
        Optional<House> found = houseRepository.findById(house.getId());
        assertTrue(found.isPresent());
        assertEquals(house.getName(), found.get().getName());
    }

    @Test
    void testFindAll() {
        assertEquals(1, houseRepository.findAll().size());
    }

    @Test
    void testDelete() {
        houseRepository.deleteById(house.getId());
        Optional<House> found = houseRepository.findById(house.getId());
        assertFalse(found.isPresent());
    }
}
