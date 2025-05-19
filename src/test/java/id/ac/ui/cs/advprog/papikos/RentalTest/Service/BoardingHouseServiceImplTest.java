package id.ac.ui.cs.advprog.papikos.house.Rental.service;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardingHouseServiceImplTest {

    @Mock private HouseRepository repo;
    @InjectMocks private BoardingHouseServiceImpl service;
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
    void create_findAll_findById_and_delete() {
        House h = baseHouse();

        // create
        when(repo.save(h)).thenReturn(h);
        House created = service.create(h);
        assertSame(h, created);
        verify(repo).save(h);

        // findAll
        when(repo.findAll()).thenReturn(List.of(h));
        List<House> all = service.findAll();
        assertEquals(1, all.size());
        assertEquals("Home", all.get(0).getName());

        // findById
        when(repo.findById(id)).thenReturn(Optional.of(h));
        Optional<House> opt = service.findById(id);
        assertTrue(opt.isPresent());
        assertEquals("123 St", opt.get().getAddress());

        // delete
        doNothing().when(repo).deleteById(id);
        assertDoesNotThrow(() -> service.delete(id));
        verify(repo).deleteById(id);
    }

    @Test
    void update_successful() {
        House stored = baseHouse();
        House changes = new House();
        changes.setName("NewName");
        changes.setAddress("456 Ave");
        changes.setDescription("Updated");
        changes.setNumberOfRooms(4);
        changes.setMonthlyRent(600.0);
        changes.setImageUrl("new.png");

        when(repo.findById(id)).thenReturn(Optional.of(stored));
        when(repo.save(any(House.class))).thenAnswer(i -> i.getArgument(0));

        House updated = service.update(id, changes);

        assertEquals("NewName", updated.getName());
        assertEquals("456 Ave", updated.getAddress());
        assertEquals("Updated", updated.getDescription());
        assertEquals(4, updated.getNumberOfRooms());
        assertEquals(600.0, updated.getMonthlyRent());
        assertEquals("new.png", updated.getImageUrl());

        verify(repo).findById(id);
        verify(repo).save(stored);
    }

    @Test
    void update_notFound_throws() {
        when(repo.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.update(id, baseHouse())
        );
        assertTrue(ex.getMessage().contains("House not found"));
        verify(repo).findById(id);
        verify(repo, never()).save(any());
    }
}
