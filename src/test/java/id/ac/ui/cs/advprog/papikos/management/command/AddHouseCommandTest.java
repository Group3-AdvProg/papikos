package id.ac.ui.cs.advprog.papikos.management.command;

import id.ac.ui.cs.advprog.papikos.house.management.command.AddHouseCommand;
import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AddHouseCommandTest {

    @Test
    void callAddHouseInService() {
        HouseManagementService mockService = mock(HouseManagementService.class);
        House house = new House("Kos A", "Jakarta", "Desc", 3, 1500000.0, "https://dummyimage.com/kos.jpg");
        AddHouseCommand command = new AddHouseCommand(mockService, house);

        command.execute();

        verify(mockService).addHouse(house);
    }
}
