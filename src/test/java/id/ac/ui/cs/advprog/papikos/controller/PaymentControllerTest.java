package id.ac.ui.cs.advprog.papikos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.service.PaymentService;
import id.ac.ui.cs.advprog.papikos.payload.request.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnSuccessWhenPaymentIsValid() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100000);
        request.setBalance(200000);
        request.setMethod("bank");

        Mockito.when(paymentService.handlePayment(Mockito.any())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/payment/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment successful"));
    }

    @Test
    void shouldReturnFailureWhenPaymentFails() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100000);
        request.setBalance(50000);
        request.setMethod("bank");

        Mockito.when(paymentService.handlePayment(Mockito.any())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/payment/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment failed or insufficient balance"));
    }
}
