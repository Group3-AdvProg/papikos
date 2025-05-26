package id.ac.ui.cs.advprog.papikos.paymentTest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.auth.filter.JwtFilter;
import id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil;
import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.rental.repository.RentalRepository;
import id.ac.ui.cs.advprog.papikos.paymentmain.controller.WalletController;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.request.PaymentRequest;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.request.TopUpRequest;
import id.ac.ui.cs.advprog.papikos.paymentmain.service.TransactionService;
import id.ac.ui.cs.advprog.papikos.paymentmain.repository.TransactionRepository;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WalletController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TransactionService.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private TransactionService transactionService;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RentalRepository rentalRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    private TopUpRequest buildRequest(String method) {
        TopUpRequest request = new TopUpRequest();
        request.setAmount(100_000);
        request.setMethod(method);
        return request;
    }

    private Principal mockPrincipal(String email) {
        return () -> email;
    }

    @Test
    void topUpBank_shouldSucceed() throws Exception {
        TopUpRequest request = buildRequest("bank");

        User user = new User();
        user.setId(1L);
        user.setBalance(0.0);
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(mockPrincipal("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Top-up successful."))
                .andExpect(jsonPath("$.redirectTo").doesNotExist());

        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void topUpVirtual_shouldSucceed() throws Exception {
        TopUpRequest request = buildRequest("virtual");

        User user = new User();
        user.setId(1L);
        user.setBalance(0.0);
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(mockPrincipal("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Top-up successful."))
                .andExpect(jsonPath("$.redirectTo").doesNotExist());

        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void topUpInvalidMethod_shouldFail() throws Exception {
        TopUpRequest request = buildRequest("crypto");

        User user = new User();
        user.setId(1L);
        user.setBalance(0.0);
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(mockPrincipal("test@example.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.message").value("Invalid top-up method."))
                .andExpect(jsonPath("$.redirectTo").value("/wallet/topup"));
    }

    @Test
    void topUp_shouldFailDueToForcedExecutionFailure() throws Exception {
        TopUpRequest request = buildRequest("bank");
        request.setAmount(9999);

        User user = new User();
        user.setId(1L);
        user.setBalance(0.0);
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(mockPrincipal("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.message").value("Top-up failed."))
                .andExpect(jsonPath("$.redirectTo").value("/wallet/topup"));
    }

    @Test
    void topUp_shouldFailWhenUserNotFound() throws Exception {
        TopUpRequest request = buildRequest("bank");
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(mockPrincipal("test@example.com")))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("User not found"));
    }

    @Test
    void getBalance_shouldReturnBalanceForValidUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setBalance(200.0);
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/wallet/balance")
                        .principal(mockPrincipal("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(content().string("200.0"));
    }

    @Test
    void getBalance_shouldFailWhenUserNotFound() throws Exception {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/wallet/balance")
                        .principal(mockPrincipal("test@example.com")))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("User not found"));
    }

    @Test
    void payRent_shouldSucceed_andMarkRentalPaid() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setTargetId(2L);
        request.setAmount(100.0);

        // prepare tenant and landlord
        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(200.0);
        tenant.setEmail("test@example.com");

        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(50.0);

        // stub the *exact* finder your controller uses:
        Rental rental = new Rental();
        rental.setId(2L);
        rental.setPaid(false);
        when(rentalRepository
                .findTopByTenantAndHouseOwnerAndIsPaidFalseOrderByIdDesc(tenant, landlord))
                .thenReturn(Optional.of(rental));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(tenant));
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(landlord));
        when(transactionRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/wallet/pay-rent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(mockPrincipal("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Rent payment successful."))
                .andExpect(jsonPath("$.redirectTo").value("/management.html"));

        // capture and verify the save
        ArgumentCaptor<Rental> captor = ArgumentCaptor.forClass(Rental.class);
        verify(rentalRepository, times(1)).save(captor.capture());
        assertTrue(captor.getValue().isPaid(), "rental must be marked as paid");
    }

    @Test
    void payRent_shouldFailWhenTenantNotFound() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setTargetId(2L);
        request.setAmount(100.0);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/wallet/pay-rent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(mockPrincipal("test@example.com")))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Tenant not found"));
    }

    @Test
    void payRent_shouldFailWhenLandlordNotFound() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setTargetId(2L);
        request.setAmount(100.0);

        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(200.0);
        tenant.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(tenant));
        when(userRepository.findById(2L))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/wallet/pay-rent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(mockPrincipal("test@example.com")))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Landlord not found"));
    }

    @Test
    void payRent_shouldFailWhenInsufficientBalance() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setTargetId(2L);
        request.setAmount(300.0);

        User tenant = new User();
        tenant.setId(1L);
        tenant.setBalance(100.0);
        tenant.setEmail("test@example.com");

        User landlord = new User();
        landlord.setId(2L);
        landlord.setBalance(50.0);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(tenant));
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(landlord));

        mockMvc.perform(post("/api/wallet/pay-rent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(mockPrincipal("test@example.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.message").value("Insufficient balance."))
                .andExpect(jsonPath("$.redirectTo").doesNotExist());
    }
}
