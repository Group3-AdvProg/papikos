package id.ac.ui.cs.advprog.papikos.management.repository;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
class HouseRepositoryTest {

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private House house;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setEmail("owner@example.com");
        owner.setPassword("securepass");
        owner.setRole("ROLE_LANDLORD");
        owner.setFullName("Test Owner");
        owner.setPhoneNumber("0811234567");
        owner = userRepository.save(owner);

        house = new House("Kos Harapan", "Depok", "Bersih dan aman", 4,
                1300000.0, "https://dummyimage.com/kos.jpg", owner);

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
        List<House> allHouses = houseRepository.findAll();
        assertEquals(1, allHouses.size());
    }

    @Test
    void testFindByOwner() {
        List<House> result = houseRepository.findByOwner(owner);
        assertEquals(1, result.size());
        assertEquals(house.getName(), result.get(0).getName());
    }

    @Test
    void testFindByIdAndOwner() {
        Optional<House> result = houseRepository.findByIdAndOwner(house.getId(), owner);
        assertTrue(result.isPresent());
        assertEquals(house.getId(), result.get().getId());
    }

    @Test
    void testDelete() {
        houseRepository.deleteById(house.getId());
        Optional<House> found = houseRepository.findById(house.getId());
        assertFalse(found.isPresent());
    }
}
