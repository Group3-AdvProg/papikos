package id.ac.ui.cs.advprog.papikos.chat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

    public enum MessageType {
        CHAT,   // a plain chat message
        JOIN,   // when a user joins the chat room
        LEAVE   // when a user leaves the chat room
    }

    // Getters and Setters
    private MessageType type;
    private String content;
    private String sender;

    // Constructors
    public ChatMessage() {}

    public ChatMessage(MessageType type, String content, String sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
    }

}
