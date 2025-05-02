package id.ac.ui.cs.advprog.papikos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String description;
    private int numberOfRooms;
    private double monthlyRent;
    private String imageUrl;

    public House(String name, String address, String description, int numberOfRooms, double monthlyRent, String imageUrl) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.numberOfRooms = numberOfRooms;
        this.monthlyRent = monthlyRent;
        this.imageUrl = imageUrl;
    }
}
