package id.ac.ui.cs.advprog.papikos.RentalTest.Service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import id.ac.ui.cs.advprog.papikos.house.rental.service.BoardingHouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BoardingHouseServiceImplTest {

    @Mock
    private HouseRepository repo;

    @InjectMocks
    private BoardingHouseServiceImpl service;

    private final Long id = 123L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private House baseHouse() {
        House h = new House();
        h.setId(id);
        h.setName("Home");
        h.setAddress("123 St");
        h.setDescription("Nice place");
        h.setNumberOfRooms(3);
        h.setMonthlyRent(500.0);
        h.setImageUrl("img.png");
        return h;
    }

    @Test
    void findAll_shouldReturnListOfHouses() {
        House h = baseHouse();
        when(repo.findAll()).thenReturn(List.of(h));

        List<House> all = service.findAll();

        assertEquals(1, all.size());
        assertEquals("Home", all.get(0).getName());
        verify(repo).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        List<House> result = service.findAll();

        assertTrue(result.isEmpty());
        verify(repo).findAll();
    }

    @Test
    void findById_shouldReturnHouse() {
        House h = baseHouse();
        when(repo.findById(id)).thenReturn(Optional.of(h));

        Optional<House> opt = service.findById(id);

        assertTrue(opt.isPresent());
        assertEquals("123 St", opt.get().getAddress());
        verify(repo).findById(id);
    }

    @Test
    void findById_shouldReturnEmptyOptional() {
        when(repo.findById(id)).thenReturn(Optional.empty());

        Optional<House> result = service.findById(id);

        assertTrue(result.isEmpty());
        verify(repo).findById(id);
    }
}
