package id.ac.ui.cs.advprog.papikos.paymentTest.controller;

import id.ac.ui.cs.advprog.papikos.paymentMain.controller.PageController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PageController.class)
public class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void topUpPage_shouldReturnTemplate() throws Exception {
        mockMvc.perform(get("/wallet/topup"))
                .andExpect(status().isOk())
                .andExpect(view().name("wallet-topup"));
    }

    @Test
    void payPage_shouldReturnTemplate() throws Exception {
        mockMvc.perform(get("/wallet/pay"))
                .andExpect(status().isOk())
                .andExpect(view().name("wallet-pay"));
    }

    @Test
    void historyPage_shouldReturnTemplate() throws Exception {
        mockMvc.perform(get("/wallet/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("wallet-history"));
    }
}
