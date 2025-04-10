package com.group3.papikos.controller;

import com.group3.papikos.payload.request.PaymentRequest;
import com.group3.papikos.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public String pay(@RequestBody PaymentRequest request) {
        boolean result = paymentService.handlePayment(request);
        return result ? "Payment successful" : "Payment failed or insufficient balance";
    }
}
