package id.ac.ui.cs.advprog.papikos.chat.controller;

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
    public ChatMessage sendMessage(@DestinationVariable Long roomId,
                                   ChatMessage message) {
        return service.saveMessage(roomId, message);
    }

    // Notify that a user joined room {roomId}
    @MessageMapping("/chat/{roomId}/addUser")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage addUser(@DestinationVariable Long roomId,
                               ChatMessage message) {
        message.setType(ChatMessage.MessageType.JOIN);
        return message;
    }
}
