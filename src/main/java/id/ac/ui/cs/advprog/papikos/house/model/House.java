package id.ac.ui.cs.advprog.papikos.house.model;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public House(String name, String address, String description, int numberOfRooms,
                 double monthlyRent, String imageUrl, User owner) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.numberOfRooms = numberOfRooms;
        this.monthlyRent = monthlyRent;
        this.imageUrl = imageUrl;
        this.owner = owner;
    }
}
