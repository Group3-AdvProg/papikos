// src/test/java/id/ac/ui/cs/advprog/papikos/chat/ChatIntegrationTest.java
package id.ac.ui.cs.advprog.papikos.chat;

import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.chat.dto.CreateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.house.Rental.model.Tenant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatIntegrationTest {

    @LocalServerPort private int port;
    @Autowired private UserRepository userRepository;

    private WebSocketStompClient stompClient;
    private String wsUrl;

    @BeforeEach
    void setup() {
        wsUrl = "ws://localhost:" + port + "/ws";
        WebSocketClient sockJs = new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))
        );
        stompClient = new WebSocketStompClient(sockJs);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @AfterEach
    void teardown() {
        stompClient.stop();
        userRepository.deleteAll();
    }

    @Test
    void testChatMessageBroadcast() throws Exception {
        // 1. Persist a Tenant (subclass of User) who will send the message
        Tenant sender = new Tenant("Test Sender", "123-456-7890");
        sender.setEmail("tester@example.com");
        sender.setPassword("pass");
        sender.setRole("TENANT");
        sender = (Tenant) userRepository.save(sender);

        // 2. Connect and subscribe
        BlockingQueue<ChatMessage> queue = new LinkedBlockingQueue<>();
        StompSession session = stompClient
                .connect(wsUrl, new StompSessionHandlerAdapter() {})  // <-- note the {}
                .get(3, TimeUnit.SECONDS);

        session.subscribe("/topic/public", new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }
            @Override public void handleFrame(StompHeaders headers, Object payload) {
                queue.offer((ChatMessage) payload);
            }
        });

        // 3. Send ChatMessage directly
        ChatMessage outgoing = new ChatMessage();
        outgoing.setType(ChatMessage.MessageType.CHAT);
        outgoing.setContent("Test Message");
        outgoing.setSender(sender);
        session.send("/app/chat.sendMessage", outgoing);

        // 4. Assert broadcast
        ChatMessage received = queue.poll(5, TimeUnit.SECONDS);
        assertNotNull(received, "Should receive a chat message");
        assertEquals("Test Message", received.getContent());
        assertEquals(sender.getId(), received.getSender().getId());
    }

    @Test
    void testAddUserBroadcast() throws Exception {
        // 1. Persist a Tenant as newUser
        Tenant newUser = new Tenant("New User", "098-765-4321");
        newUser.setEmail("new@example.com");
        newUser.setPassword("pass");
        newUser.setRole("TENANT");
        newUser = (Tenant) userRepository.save(newUser);

        // 2. Connect and subscribe
        BlockingQueue<ChatMessage> queue = new LinkedBlockingQueue<>();
        StompSession session = stompClient
                .connect(wsUrl, new StompSessionHandlerAdapter() {})  // <-- and here
                .get(3, TimeUnit.SECONDS);

        session.subscribe("/topic/public", new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }
            @Override public void handleFrame(StompHeaders headers, Object payload) {
                queue.offer((ChatMessage) payload);
            }
        });

        // 3. Send join event
        CreateMessageRequest req = new CreateMessageRequest();
        req.setSenderId(newUser.getId());
        req.setContent("User joined");
        session.send("/app/chat.addUser", req);

        // 4. Assert JOIN broadcast
        ChatMessage resp = queue.poll(5, TimeUnit.SECONDS);
        assertNotNull(resp, "Should receive a join message");
        assertEquals(ChatMessage.MessageType.JOIN, resp.getType());
        assertEquals("User joined", resp.getContent());
        assertNull(resp.getSender(), "Join message does not include sender");
    }
}
