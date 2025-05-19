package id.ac.ui.cs.advprog.papikos.paymentTest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.paymentMain.model.User;
import id.ac.ui.cs.advprog.papikos.paymentMain.payload.request.TopUpRequest;
import id.ac.ui.cs.advprog.papikos.paymentMain.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.paymentMain.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private UserRepository userRepository;

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
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Top-up successful."))
                .andExpect(jsonPath("$.redirectTo").doesNotExist());
    }

    @Test
    void topUpVirtual_shouldSucceed() throws Exception {
        TopUpRequest request = buildRequest("virtual");

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Top-up successful."))
                .andExpect(jsonPath("$.redirectTo").doesNotExist());
    }

    @Test
    void topUpInvalidMethod_shouldFail() throws Exception {
        TopUpRequest request = buildRequest("crypto");

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.message").value("Invalid top-up method."))
                .andExpect(jsonPath("$.redirectTo").value("/wallet/topup"));
    }

    @Test
    void getBalance_shouldReturnBalanceForValidUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setBalance(200.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/wallet/balance")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("200.0"));
    }

    @Test
    void getBalance_shouldFailWhenUserNotFound() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/wallet/balance")
                        .param("userId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("User not found"));
    }

    @Test
    void topUp_shouldFailDueToForcedExecutionFailure() throws Exception {
        TopUpRequest request = buildRequest("bank");
        request.setAmount(9999); // Triggers the forced failure in controller

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.message").value("Top-up failed."))
                .andExpect(jsonPath("$.redirectTo").value("/wallet/topup"));
    }


}
