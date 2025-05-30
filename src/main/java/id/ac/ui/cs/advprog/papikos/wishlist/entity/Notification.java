package id.ac.ui.cs.advprog.papikos.wishlist.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long receiverId;  // receiver
    private Long senderId;   // sender
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
}
