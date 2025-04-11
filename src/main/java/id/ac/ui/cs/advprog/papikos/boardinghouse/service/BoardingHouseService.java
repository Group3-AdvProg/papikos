package id.ac.ui.cs.advprog.papikos.boardinghouse.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BoardingHouseService {

    private Map<String, Boolean> availabilityMap = new HashMap<>();

    public void setAvailability(String propertyId, boolean isAvailable) {
        availabilityMap.put(propertyId, isAvailable);
    }

    public boolean isAvailable(String propertyId) {
        return availabilityMap.getOrDefault(propertyId, false);
    }

    public String getBoardingHouseName(String propertyId) {
        return "Sample House for ID " + propertyId;
    }

    public String getAvailabilityStatusText(String propertyId) {
        return isAvailable(propertyId) ? "Room Available" : "Fully Booked";
    }
}
