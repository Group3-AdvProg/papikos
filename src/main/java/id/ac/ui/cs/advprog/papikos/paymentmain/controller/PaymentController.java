package id.ac.ui.cs.advprog.papikos.paymentmain.controller;

import id.ac.ui.cs.advprog.papikos.paymentmain.service.PaymentService;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.request.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Async
    @PostMapping("/pay")
    public CompletableFuture<String> pay(@RequestBody PaymentRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            boolean result = paymentService.handlePayment(request);
            return result ? "payment successful" : "payment failed or insufficient balance";
        });
    }
}
