package id.ac.ui.cs.advprog.papikos.management.command;

import id.ac.ui.cs.advprog.papikos.management.service.HouseManagementService;

public class DeleteHouseCommand implements HouseManagementCommand {

    private final HouseManagementService service;
    private final Long id;

    public DeleteHouseCommand(HouseManagementService service, Long id) {
        this.service = service;
        this.id = id;
    }

    @Override
    public void execute() {
        service.deleteHouse(id);
    }
}
