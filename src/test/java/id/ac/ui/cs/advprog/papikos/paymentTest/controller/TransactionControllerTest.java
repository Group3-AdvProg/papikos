package id.ac.ui.cs.advprog.papikos.paymentTest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.paymentmain.controller.TransactionController;
import id.ac.ui.cs.advprog.papikos.paymentmain.model.Transaction;
import id.ac.ui.cs.advprog.papikos.paymentmain.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        id.ac.ui.cs.advprog.papikos.auth.filter.JwtFilter.class
    })
})
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getTransactionsByUser_shouldReturnTransactions() throws Exception {
        User user = new User();
        user.setId(1L);
        Transaction tx = new Transaction();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(transactionService.getTransactionsByUserOrTarget(user)).thenReturn(Collections.singletonList(tx));

        mockMvc.perform(get("/api/transaction/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactionsByUser_shouldReturn404IfUserNotFound() throws Exception {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/transaction/user/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getTransactionsByType_shouldReturnTransactions() throws Exception {
        User user = new User();
        user.setId(1L);
        Transaction tx = new Transaction();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(transactionService.getTransactionsByUserAndType(user, "RENT")).thenReturn(Arrays.asList(tx));

        mockMvc.perform(get("/api/transaction/type")
                        .param("userId", "1")
                        .param("type", "RENT"))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactionsByType_shouldReturn404IfUserNotFound() throws Exception {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/transaction/type")
                .param("userId", "1")
                .param("type", "RENT"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getTransactionsByDate_shouldReturnTransactions() throws Exception {
        User user = new User();
        user.setId(1L);
        Transaction tx = new Transaction();
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(transactionService.getTransactionsByUserAndDate(eq(user), any(), any(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(tx)));

        mockMvc.perform(get("/api/transaction/date")
                        .param("userId", "1")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactionsByDate_shouldReturn404IfUserNotFound() throws Exception {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/transaction/date")
                .param("userId", "1")
                .param("from", LocalDateTime.now().minusDays(1).toString())
                .param("to", LocalDateTime.now().toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    void getTransactionsByFilter_shouldReturnTransactions() throws Exception {
        User user = new User();
        user.setId(1L);
        Transaction tx = new Transaction();
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(transactionService.getTransactionsByUserTypeAndDate(eq(user), eq("RENT"), any(), any(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(tx)));

        mockMvc.perform(get("/api/transaction/filter")
                        .param("userId", "1")
                        .param("type", "RENT")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactionsByFilter_shouldReturn404IfUserNotFound() throws Exception {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/transaction/filter")
                .param("userId", "1")
                .param("type", "RENT")
                .param("from", LocalDateTime.now().minusDays(1).toString())
                .param("to", LocalDateTime.now().toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    void getTransactionsByUserOrTarget_shouldReturnTransactions() throws Exception {
        User user = new User();
        user.setId(1L);
        Transaction tx = new Transaction();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(transactionService.getTransactionsByUserOrTarget(user)).thenReturn(Collections.singletonList(tx));

        mockMvc.perform(get("/api/transaction/user-or-target/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactionsByUserOrTarget_shouldReturn404IfUserNotFound() throws Exception {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/transaction/user-or-target/1"))
                .andExpect(status().isNotFound());
    }
}