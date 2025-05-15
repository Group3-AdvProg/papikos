package id.ac.ui.cs.advprog.papikos.payload.request;

import lombok.Data;

@Data
public class TopUpRequest {
    private String userId;
    private double amount;
    private String method; // "bank" or "virtual"
}
