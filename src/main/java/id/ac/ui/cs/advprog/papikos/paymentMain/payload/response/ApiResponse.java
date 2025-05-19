package id.ac.ui.cs.advprog.papikos.paymentMain.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String status;      // "SUCCESS" or "FAILED"
    private String message;     // Human-readable message
    private String redirectTo;  // Optional URL path to redirect
}
