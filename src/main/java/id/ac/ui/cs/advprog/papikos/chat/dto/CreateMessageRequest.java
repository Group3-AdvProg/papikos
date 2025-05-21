// src/main/java/id/ac/ui/cs/advprog/papikos/chat/dto/CreateMessageRequest.java
package id.ac.ui.cs.advprog.papikos.chat.dto;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateMessageRequest {
    private Long senderId;
    private ChatMessage.MessageType type;
    private String content;

}
