package id.ac.ui.cs.advprog.papikos.management.command;

import org.springframework.stereotype.Component;

@Component
public class HouseCommandExecutor {
    public void execute(HouseCommand command) {
        command.execute();
    }
}
