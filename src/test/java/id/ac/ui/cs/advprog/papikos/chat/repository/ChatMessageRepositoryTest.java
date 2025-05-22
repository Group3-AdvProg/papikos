package id.ac.ui.cs.advprog.papikos.chat.repository;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
class ChatMessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChatMessageRepository repository;

    @Test
    void save_setsIdRoomAndTimestamp_andFindByIdWorks() {
        // 1. Persist tenant and landlord as Tenant entities
        Tenant tenant = new Tenant("Test Tenant", "111-222-3333");
        tenant.setEmail("tenant@example.com");
        tenant.setPassword("pass");
        tenant.setRole("TENANT");
        tenant = entityManager.persistAndFlush(tenant);

        Tenant landlord = new Tenant("Test Landlord", "444-555-6666");
        landlord.setEmail("landlord@example.com");
        landlord.setPassword("pass");
        landlord.setRole("LANDLORD");
        landlord = entityManager.persistAndFlush(landlord);

        // 2. Create and persist a ChatRoom
        ChatRoom room = ChatRoom.builder()
                .tenant(tenant)
                .landlord(landlord)
                .build();
        room = entityManager.persistAndFlush(room);

        // 3. Persist a sender as Tenant
        Tenant sender = new Tenant("Sender User", "777-888-9999");
        sender.setEmail("sender@example.com");
        sender.setPassword("pass");
        sender.setRole("TENANT");
        sender = entityManager.persistAndFlush(sender);

        // 4. Build a ChatMessage linked to that room and sender
        ChatMessage msg = ChatMessage.builder()
                .type(ChatMessage.MessageType.JOIN)
                .content("User joined")
                .sender(sender)
                .room(room)
                .build();

        // 5. Save via repository (triggers @PrePersist)
        ChatMessage saved = repository.save(msg);

        // 6. Verify id, room, sender, and timestamp are set
        assertNotNull(saved.getId(), "ID should be generated");
        assertEquals(room.getId(), saved.getRoom().getId(), "Room ID must match");
        assertEquals(sender.getId(), saved.getSender().getId(), "Sender ID must match");
        assertNotNull(saved.getTimestamp(), "Timestamp should be set by @PrePersist");

        // 7. Clear and fetch from DB to ensure it round-trips
        entityManager.clear();
        Optional<ChatMessage> fetched = repository.findById(saved.getId());
        assertTrue(fetched.isPresent(), "Should retrieve by ID");
        assertEquals("User joined", fetched.get().getContent(), "Content must match");
        assertEquals(sender.getId(), fetched.get().getSender().getId(), "Sender must match");
    }
}
