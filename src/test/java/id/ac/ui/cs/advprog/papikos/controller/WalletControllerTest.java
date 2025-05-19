package id.ac.ui.cs.advprog.papikos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.payload.request.TopUpRequest;
import id.ac.ui.cs.advprog.papikos.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private TopUpRequest buildRequest(String method) {
        TopUpRequest request = new TopUpRequest();
        request.setUserId("tenant123");
        request.setAmount(100_000);
        request.setMethod(method);
        return request;
    }

    @Test
    void topUpBank_shouldSucceed() throws Exception {
        TopUpRequest request = buildRequest("bank");

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Top-up successful."));
    }

    @Test
    void topUpVirtual_shouldSucceed() throws Exception {
        TopUpRequest request = buildRequest("virtual");

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Top-up successful."));
    }

    @Test
    void topUpInvalidMethod_shouldFail() throws Exception {
        TopUpRequest request = buildRequest("crypto");

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid top-up method."));
    }
}
