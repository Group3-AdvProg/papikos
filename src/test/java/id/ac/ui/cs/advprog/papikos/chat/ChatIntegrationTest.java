package id.ac.ui.cs.advprog.papikos.chat;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
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

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private String wsUrl;

    @BeforeEach
    void setup() {
        wsUrl = "ws://localhost:" + port + "/ws";
        List<Transport> transports = List.of(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient sockJsClient = new SockJsClient(transports);

        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @AfterEach
    void teardown() {
        if (stompClient != null) {
            stompClient.stop();
        }
    }

    @Test
    void testChatMessageBroadcast() throws Exception {
        BlockingQueue<ChatMessage> blockingQueue = new LinkedBlockingQueue<>();

        // Minimal session handler
        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};

        StompSession session = stompClient.connect(wsUrl, sessionHandler).get(3, TimeUnit.SECONDS);

        // Subscribe to /topic/public to capture broadcast messages
        session.subscribe("/topic/public", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.offer((ChatMessage) payload);
            }
        });

        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MessageType.CHAT);
        message.setContent("Test Message");
        message.setSender("tester");

        session.send("/app/chat.sendMessage", message);

        ChatMessage response = blockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response, "Should receive a chat message broadcast");
        assertEquals("Test Message", response.getContent(), "Message content should match");
    }

    @Test
    void testAddUserBroadcast() throws Exception {
        BlockingQueue<ChatMessage> blockingQueue = new LinkedBlockingQueue<>();

        // Use a minimal session handler.
        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};

        StompSession session = stompClient.connect(wsUrl, sessionHandler).get(3, TimeUnit.SECONDS);

        // Subscribe to /topic/public to capture broadcast messages.
        session.subscribe("/topic/public", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.offer((ChatMessage) payload);
            }
        });

        // Create a sample chat message for a user joining.
        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setType(ChatMessage.MessageType.JOIN);
        joinMessage.setContent("User joined");
        joinMessage.setSender("newUser");

        // Send the join message via the /chat.addUser endpoint.
        session.send("/app/chat.addUser", joinMessage);

        ChatMessage response = blockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response, "Should receive a chat message broadcast for added user");
        assertEquals("newUser", response.getSender(), "Sender should be 'newUser'");
        assertEquals("User joined", response.getContent(), "Content should indicate join message");
        assertEquals(ChatMessage.MessageType.JOIN, response.getType(), "Message type should be JOIN");
    }
}
