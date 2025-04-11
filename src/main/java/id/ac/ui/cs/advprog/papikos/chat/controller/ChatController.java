package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    /**
     * This method listens for incoming messages sent to the "/app/chat.sendMessage" destination
     * and broadcasts them to all subscribers of "/topic/public".
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        // In a real application you might add additional processing here (e.g., saving the message to a database)
        return chatMessage;
    }

    /**
     * This method handles events when a new user is added to the chat.
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage chatMessage) {
        // You can include logic to track connected users or broadcast a "user joined" message
        return chatMessage;
    }
}
