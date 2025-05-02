package id.ac.ui.cs.advprog.papikos.wishlist.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenantId;
    private String message;
    private LocalDateTime createdAt;

    private boolean isRead;

    public LocalTime getTimestamp() {
        return null;
    }
}
