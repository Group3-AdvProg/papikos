package id.ac.ui.cs.advprog.papikos.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String email;
    private double balance;
    private String role;
}
