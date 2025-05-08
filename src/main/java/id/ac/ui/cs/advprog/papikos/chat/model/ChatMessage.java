package id.ac.ui.cs.advprog.papikos.chat.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "chat_messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = Instant.now();
    }
}
