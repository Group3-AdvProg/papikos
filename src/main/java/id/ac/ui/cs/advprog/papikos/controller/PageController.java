package id.ac.ui.cs.advprog.papikos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/wallet/topup")
    public String topUpPage() {
        return "wallet-topup"; // resolves to templates/wallet-topup.html
    }

    @GetMapping("/wallet/pay")
    public String payPage() {
        return "wallet-pay";
    }

    @GetMapping("/wallet/history")
    public String historyPage() {
        return "wallet-history";
    }
}
