package id.ac.ui.cs.advprog.papikos.house.management.command;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementService;

public class AddHouseCommand implements HouseManagementCommand {

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
