package id.ac.ui.cs.advprog.papikos.chat.repository;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
class ChatRoomRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChatRoomRepository repository;

    @Test
    void saveAndFindAll_roundTripsEntities() {
        // first room participants
        Tenant tenant1 = new Tenant("Tenant One", "111-111-1111");
        tenant1.setEmail("tenant1@example.com");
        tenant1.setPassword("pass");
        tenant1.setRole("TENANT");
        entityManager.persistAndFlush(tenant1);

        Tenant landlord1 = new Tenant("Landlord One", "222-222-2222");
        landlord1.setEmail("landlord1@example.com");
        landlord1.setPassword("pass");
        landlord1.setRole("LANDLORD");
        entityManager.persistAndFlush(landlord1);

        repository.save(ChatRoom.builder()
                .tenant(tenant1)
                .landlord(landlord1)
                .build());

        // second room participants
        Tenant tenant2 = new Tenant("Tenant Two", "333-333-3333");
        tenant2.setEmail("tenant2@example.com");
        tenant2.setPassword("pass");
        tenant2.setRole("TENANT");
        entityManager.persistAndFlush(tenant2);

        Tenant landlord2 = new Tenant("Landlord Two", "444-444-4444");
        landlord2.setEmail("landlord2@example.com");
        landlord2.setPassword("pass");
        landlord2.setRole("LANDLORD");
        entityManager.persistAndFlush(landlord2);

        repository.save(ChatRoom.builder()
                .tenant(tenant2)
                .landlord(landlord2)
                .build());

        // verify
        List<ChatRoom> rooms = repository.findAll();
        assertThat(rooms).hasSize(2);

        assertThat(rooms)
                .extracting(r -> r.getTenant().getId())
                .containsExactlyInAnyOrder(tenant1.getId(), tenant2.getId());

        assertThat(rooms)
                .extracting(r -> r.getLandlord().getId())
                .containsExactlyInAnyOrder(landlord1.getId(), landlord2.getId());

        // THIS LINE now compiles and checks createdAt != null
        assertThat(rooms)
                .extracting(ChatRoom::getCreatedAt)
                .doesNotContainNull();
    }
}
