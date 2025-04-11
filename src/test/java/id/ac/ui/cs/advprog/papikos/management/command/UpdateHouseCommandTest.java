package id.ac.ui.cs.advprog.papikos.management.command;

import id.ac.ui.cs.advprog.papikos.management.model.House;
import id.ac.ui.cs.advprog.papikos.management.service.HouseManagementService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class UpdateHouseCommandTest {

    @Test
    void callUpdateHouseInService() {
        HouseManagementService mockService = mock(HouseManagementService.class);
        House house = new House(1L, "New Kos", "Bandung", "New Desc", 4, 1600000.0);
        UpdateHouseCommand command = new UpdateHouseCommand(mockService, 1L, house);

        command.execute();

        verify(mockService).updateHouse(1L, house);
    }
}
