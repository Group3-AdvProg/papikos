package id.ac.ui.cs.advprog.papikos.paymentTest.controller;

import id.ac.ui.cs.advprog.papikos.paymentmain.controller.LoggingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = LoggingController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "id\\.ac\\.ui\\.cs\\.advprog\\.papikos\\.auth\\..*")
)
public class LoggingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void logClientError_shouldReturnOk() throws Exception {
        String errorMessage = "Test error from client";
        mockMvc.perform(post("/api/log")
                .contentType(MediaType.TEXT_PLAIN)
                .content(errorMessage))
                .andExpect(status().isOk());
    }
}