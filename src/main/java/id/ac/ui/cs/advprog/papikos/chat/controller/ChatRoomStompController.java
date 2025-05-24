package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.dto.CreateMessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatRoomStompController {

    private static final Logger logger = LoggerFactory.getLogger(ChatRoomStompController.class);

    private final ChatRoomService service;

    public ChatRoomStompController(ChatRoomService service) {
        this.service = service;
    }

    @MessageMapping("/chat/{roomId}/sendMessage")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage sendMessage(
            @DestinationVariable Long roomId,
            CreateMessageRequest req) {

        logger.info("STOMP sendMessage received for room [{}] from [{}]: {}",
                roomId, req.getSenderEmail(), req.getContent());

        ChatMessage msg = ChatMessage.builder()
                .type(req.getType())
                .content(req.getContent())
                .build();

        ChatMessage saved = service
                .saveMessage(roomId, req.getSenderEmail(), msg)
                .join();

        logger.info("Message [{}] saved in room [{}]", saved.getId(), roomId);
        return saved;
    }

    @MessageMapping("/chat/{roomId}/addUser")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage addUser(
            @DestinationVariable Long roomId,
            CreateMessageRequest req) {

        logger.info("STOMP addUser received for room [{}]: {}", roomId, req.getSenderEmail());

        ChatMessage joinMsg = ChatMessage.builder()
                .type(ChatMessage.MessageType.JOIN)
                .content(req.getContent())
                .build();

        logger.info("Broadcasting JOIN for user [{}] into room [{}]", req.getSenderEmail(), roomId);
        return joinMsg;
    }
}
