package id.ac.ui.cs.advprog.papikos.paymentTest.controller;

import id.ac.ui.cs.advprog.papikos.auth.filter.JwtFilter;
import id.ac.ui.cs.advprog.papikos.auth.util.JwtUtil;
import id.ac.ui.cs.advprog.papikos.paymentMain.controller.PageController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(controllers = PageController.class)
@AutoConfigureMockMvc(addFilters = false) // disables filters like JwtFilter
public class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;


    @Test
    void topUpPage_shouldReturnTemplate() throws Exception {
        mockMvc.perform(get("/wallet/topup"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/wallet-topup.html"));
    }

    @Test
    void payPage_shouldReturnTemplate() throws Exception {
        mockMvc.perform(get("/wallet/pay"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/wallet-pay.html"));
    }

    @Test
    void historyPage_shouldReturnTemplate() throws Exception {
        mockMvc.perform(get("/wallet/history"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/wallet-history.html"));
    }
}
