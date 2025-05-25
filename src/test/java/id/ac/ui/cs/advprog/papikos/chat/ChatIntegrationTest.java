// src/test/java/id/ac/ui/cs/advprog/papikos/chat/ChatIntegrationTest.java
package id.ac.ui.cs.advprog.papikos.chat;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ChatIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    private WebSocketStompClient stompClient;
    private String wsUrl;

    @BeforeEach
    void setup() {
        // use SockJS so the server’s /ws endpoint will upgrade successfully
        wsUrl = "http://localhost:" + port + "/ws";
        stompClient = new WebSocketStompClient(
                new SockJsClient(
                        List.of(new WebSocketTransport(new StandardWebSocketClient()))
                )
        );
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    void testPublicChatBroadcast() throws Exception {
        // persist a user
        User sender = new User();
        sender.setFullName("Test Sender");
        sender.setPhoneNumber("123-456-7890");
        sender.setEmail("tester@example.com");
        sender.setPassword("pass");
        sender.setRole("TENANT");
        sender = userRepository.saveAndFlush(sender);

        // subscribe to /topic/public
        BlockingQueue<ChatMessage> queue = new LinkedBlockingQueue<>();
        StompSession session = stompClient
                .connect(wsUrl, new StompSessionHandlerAdapter() {})
                .get(3, TimeUnit.SECONDS);

        session.subscribe("/topic/public", new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }
            @Override public void handleFrame(StompHeaders headers, Object payload) {
                queue.offer((ChatMessage) payload);
            }
        });

        // Wait until the subscription is likely active by polling the queue with a timeout
        // (No message expected yet, but ensures subscription is ready)
        queue.poll(500, TimeUnit.MILLISECONDS);

        // send a raw ChatMessage
        ChatMessage outgoing = new ChatMessage();
        outgoing.setType(ChatMessage.MessageType.CHAT);
        outgoing.setContent("Hello public!");
        outgoing.setSender(sender);

        session.send("/app/chat.sendMessage", outgoing);

        ChatMessage received = queue.poll(5, TimeUnit.SECONDS);
        assertNotNull(received, "Should receive a message on /topic/public");
        assertEquals("Hello public!", received.getContent());
        assertEquals(sender.getId(), received.getSender().getId());
    }

    @Test
    void testPublicAddUserBroadcast() throws Exception {
        User newUser = new User();
        newUser.setFullName("New User");
        newUser.setPhoneNumber("098-765-4321");
        newUser.setEmail("new@example.com");
        newUser.setPassword("pass");
        newUser.setRole("TENANT");
        newUser = userRepository.saveAndFlush(newUser);

        BlockingQueue<ChatMessage> queue = new LinkedBlockingQueue<>();
        StompSession session = stompClient
                .connect(wsUrl, new StompSessionHandlerAdapter() {})
                .get(3, TimeUnit.SECONDS);

        session.subscribe("/topic/public", new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }
            @Override public void handleFrame(StompHeaders headers, Object payload) {
                queue.offer((ChatMessage) payload);
            }
        });

        // Wait until the subscription is likely active by polling the queue with a timeout
        queue.poll(500, TimeUnit.MILLISECONDS);

        ChatMessage joinMsg = new ChatMessage();
        joinMsg.setType(ChatMessage.MessageType.JOIN);
        joinMsg.setContent("User joined");
        session.send("/app/chat.addUser", joinMsg);

        ChatMessage resp = queue.poll(5, TimeUnit.SECONDS);
        assertNotNull(resp, "Should receive a join message");
        assertEquals(ChatMessage.MessageType.JOIN, resp.getType());
        assertEquals("User joined", resp.getContent());
        assertNull(resp.getSender(), "Join message should have no sender");
    }
}
