// src/main/java/id/ac/ui/cs/advprog/papikos/chat/dto/CreateRoomRequest.java
package id.ac.ui.cs.advprog.papikos.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateRoomRequest {
    private Long tenantId;
    private Long landlordId;

}
