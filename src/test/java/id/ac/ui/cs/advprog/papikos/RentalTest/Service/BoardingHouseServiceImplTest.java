package id.ac.ui.cs.advprog.papikos.RentalTest.Service;

import id.ac.ui.cs.advprog.papikos.house.Rental.service.BoardingHouseServiceImpl;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.repository.HouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardingHouseServiceImplTest {

    @Mock private HouseRepository repo;
    @InjectMocks private BoardingHouseServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        House h = new House("N","A","D",1,100.0,"I");
        when(repo.save(h)).thenReturn(new House(1L,"N","A","D",1,100.0,"I"));
        House result = service.create(h);
        assertNotNull(result.getId());
        assertEquals("N", result.getName());
    }

    @Test
    void testFindAll() {
        House h1 = new House(1L,"A","X","D",2,50.0,"U");
        when(repo.findAll()).thenReturn(Arrays.asList(h1));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById() {
        House h = new House(2L,"B","Y","D",3,75.0,"U");
        when(repo.findById(2L)).thenReturn(Optional.of(h));
        Optional<House> opt = service.findById(2L);
        assertTrue(opt.isPresent());
        assertEquals("B", opt.get().getName());
    }

    @Test
    void testUpdate() {
        House existing = new House(3L,"Old","O","D",1,20.0,"U");
        House upd = new House("New","N","D",2,40.0,"I");
        when(repo.findById(3L)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        House result = service.update(3L, upd);
        assertEquals("New", result.getName());
        assertEquals(2, result.getNumberOfRooms());
    }

    @Test
    void testDelete() {
        doNothing().when(repo).deleteById(4L);
        assertDoesNotThrow(() -> service.delete(4L));
        verify(repo).deleteById(4L);
    }
}
