package id.ac.ui.cs.advprog.papikos.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthRequest {
    private String email;
    private String password;
}
