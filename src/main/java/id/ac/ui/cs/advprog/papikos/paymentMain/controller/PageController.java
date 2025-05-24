package id.ac.ui.cs.advprog.papikos.paymentMain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/wallet/history")
    public String historyPage() {
        return "redirect:/wallet-history.html";
    }
}
