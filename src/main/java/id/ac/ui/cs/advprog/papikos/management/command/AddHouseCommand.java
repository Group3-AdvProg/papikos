package id.ac.ui.cs.advprog.papikos.management.command;

import id.ac.ui.cs.advprog.papikos.management.model.House;
import id.ac.ui.cs.advprog.papikos.management.service.HouseManagementService;

public class AddHouseCommand implements HouseCommand {

    private final HouseManagementService service;
    private final House house;

    public AddHouseCommand(HouseManagementService service, House house) {
        this.service = service;
        this.house = house;
    }

    @Override
    public void execute() {
        service.addHouse(house);
    }
}
