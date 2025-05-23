// src/main/java/id/ac/ui/cs/advprog/papikos/chat/dto/CreateMessageRequest.java
package id.ac.ui.cs.advprog.papikos.chat.dto;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateMessageRequest {
    private String senderEmail;                 // ← was Long senderId
    private ChatMessage.MessageType type;
    private String content;
}
