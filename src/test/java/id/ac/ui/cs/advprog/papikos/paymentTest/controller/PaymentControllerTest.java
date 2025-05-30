package id.ac.ui.cs.advprog.papikos.paymentTest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.auth.filter.JwtFilter;
import id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil;
import id.ac.ui.cs.advprog.papikos.paymentmain.controller.PaymentController;
import id.ac.ui.cs.advprog.papikos.paymentmain.service.PaymentService;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.request.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false) //
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void shouldReturnSuccessWhenPaymentIsValid() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100_000.0);
        request.setMethod("bank");

        Mockito.when(paymentService.handlePayment(Mockito.any())).thenReturn(true);

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/payment/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("payment successful"));
    }

    @Test
    void shouldReturnFailureWhenPaymentFails() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100_000.0);
        request.setMethod("bank");

        Mockito.when(paymentService.handlePayment(Mockito.any())).thenReturn(false);

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/payment/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("payment failed or insufficient balance"));
    }
}
