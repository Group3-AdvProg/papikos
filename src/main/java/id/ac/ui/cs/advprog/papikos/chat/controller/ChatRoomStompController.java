// src/main/java/id/ac/ui/cs/advprog/papikos/chat/controller/ChatRoomStompController.java
package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.dto.CreateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

@Controller
public class ChatRoomStompController {
    private final ChatRoomService service;

    public ChatRoomStompController(ChatRoomService service) {
        this.service = service;
    }

    // Send a chat message within room {roomId}
    @MessageMapping("/chat/{roomId}/sendMessage")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage sendMessage(
            @DestinationVariable Long roomId,
            CreateMessageRequest req) {

        ChatMessage msg = ChatMessage.builder()
                .type(req.getType())
                .content(req.getContent())
                .build();
        return service.saveMessage(roomId, req.getSenderId(), msg);
    }

    // Notify that a user joined room {roomId}
    @MessageMapping("/chat/{roomId}/addUser")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage addUser(
            @DestinationVariable Long roomId,
            CreateMessageRequest req) {

        // only set JOIN type; service not required
        return ChatMessage.builder()
                .type(ChatMessage.MessageType.JOIN)
                .content(req.getContent())
                .build();
    }
}
