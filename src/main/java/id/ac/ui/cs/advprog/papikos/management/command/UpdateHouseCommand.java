package id.ac.ui.cs.advprog.papikos.management.command;

import id.ac.ui.cs.advprog.papikos.model.House;
import id.ac.ui.cs.advprog.papikos.management.service.HouseManagementService;

public class UpdateHouseCommand implements HouseManagementCommand {

    private final HouseManagementService service;
    private final Long id;
    private final House updated;

    public UpdateHouseCommand(HouseManagementService service, Long id, House updated) {
        this.service = service;
        this.id = id;
        this.updated = updated;
    }

    @Override
    public void execute() {
        service.updateHouse(id, updated);
    }
}
