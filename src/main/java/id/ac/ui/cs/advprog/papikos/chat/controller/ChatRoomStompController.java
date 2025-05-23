package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.dto.CreateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatRoomStompController {
    private final ChatRoomService service;

    public ChatRoomStompController(ChatRoomService service) {
        this.service = service;
    }

    @MessageMapping("/chat/{roomId}/sendMessage")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage sendMessage(
            @DestinationVariable Long roomId,
            CreateMessageRequest req) {

        ChatMessage msg = ChatMessage.builder()
                .type(req.getType())
                .content(req.getContent())
                .build();

        // block here so STOMP has a concrete ChatMessage to send immediately
        return service
                .saveMessage(roomId, req.getSenderEmail(), msg)
                .join();
    }

    @MessageMapping("/chat/{roomId}/addUser")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage addUser(
            @DestinationVariable Long roomId,
            CreateMessageRequest req) {

        return ChatMessage.builder()
                .type(ChatMessage.MessageType.JOIN)
                .content(req.getContent())
                .build();
    }
}
