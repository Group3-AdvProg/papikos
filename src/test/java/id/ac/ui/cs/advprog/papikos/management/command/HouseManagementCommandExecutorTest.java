package id.ac.ui.cs.advprog.papikos.management.command;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class HouseManagementCommandExecutorTest {

    @Test
    void testExecuteCommand() {
        HouseManagementCommand command = mock(HouseManagementCommand.class);
        HouseManagementCommandExecutor executor = new HouseManagementCommandExecutor();

        executor.execute(command);

        verify(command, times(1)).execute();
    }
}
