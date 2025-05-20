// src/main/java/id/ac/ui/cs/advprog/papikos/chat/service/ChatRoomService.java
package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatMessage;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatMessageRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ChatRoomService {
    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;

    public ChatRoomService(ChatRoomRepository roomRepo,
                           ChatMessageRepository msgRepo) {
        this.roomRepo = roomRepo;
        this.msgRepo = msgRepo;
    }

    public ChatRoom createRoom(String name) {
        ChatRoom room = ChatRoom.builder().name(name).build();
        return roomRepo.save(room);
    }

    public List<ChatRoom> listRooms() {
        return roomRepo.findAll(Sort.by("createdAt"));
    }

    public ChatMessage saveMessage(Long roomId, ChatMessage msg) {
        ChatRoom room = roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found: " + roomId));
        msg.setRoom(room);
        return msgRepo.save(msg);
    }

    public List<ChatMessage> listMessages(Long roomId) {
        return msgRepo.findByRoomIdOrderByTimestampAsc(roomId);
    }
}
