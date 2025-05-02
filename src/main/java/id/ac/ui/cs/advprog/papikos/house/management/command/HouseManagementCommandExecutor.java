package id.ac.ui.cs.advprog.papikos.house.management.command;

import org.springframework.stereotype.Component;

@Component
public class HouseManagementCommandExecutor {
    public void execute(HouseManagementCommand command) {
        command.execute();
    }
}
