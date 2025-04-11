package id.ac.ui.cs.advprog.papikos.chat.model;

public class ChatMessage {

    public enum MessageType {
        CHAT,   // a plain chat message
        JOIN,   // when a user joins the chat room
        LEAVE   // when a user leaves the chat room
    }

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

    // Getters and Setters
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
