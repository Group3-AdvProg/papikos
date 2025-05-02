package id.ac.ui.cs.advprog.papikos.management.command;

import id.ac.ui.cs.advprog.papikos.house.management.command.DeleteHouseCommand;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DeleteHouseCommandTest {

    @Test
    void callDeleteHouseInService() {
        HouseManagementService mockService = mock(HouseManagementService.class);
        DeleteHouseCommand command = new DeleteHouseCommand(mockService, 1L);

        command.execute();

        verify(mockService).deleteHouse(1L);
    }
}
