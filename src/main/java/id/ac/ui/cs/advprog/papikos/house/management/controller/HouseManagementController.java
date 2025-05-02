package id.ac.ui.cs.advprog.papikos.house.management.controller;

import id.ac.ui.cs.advprog.papikos.house.model.House;
import id.ac.ui.cs.advprog.papikos.house.management.service.HouseManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/management")
public class HouseManagementController {

    @Autowired
    private HouseManagementService houseManagementService;

    @GetMapping("/create")
    public String createHousePage(Model model) {
        House house = new House();
        model.addAttribute("house", house);
        return "CreateHouse";
    }

    @PostMapping("/create")
    public String createHousePost(@ModelAttribute House house, Model model) {
        houseManagementService.addHouse(house);
        return "redirect:/management/list";
    }

    @GetMapping("/list")
    public String houseListPage(Model model) {
        List<House> allHouses = houseManagementService.findAll();
        model.addAttribute("houses", allHouses);
        return "Management";
    }

    @GetMapping("/edit/{id}")
    public String editHousePage(@PathVariable Long id, Model model) {
        House house = houseManagementService.findById(id).orElse(null);
        model.addAttribute("house", house);
        return "EditHouse";
    }

    @PostMapping("/edit")
    public String editHousePost(@ModelAttribute House house, Model model) {
        houseManagementService.updateHouse(house.getId(), house);
        return "redirect:/management/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteHouse(@PathVariable Long id) {
        houseManagementService.deleteHouse(id);
        return "redirect:/management/list";
    }
}
