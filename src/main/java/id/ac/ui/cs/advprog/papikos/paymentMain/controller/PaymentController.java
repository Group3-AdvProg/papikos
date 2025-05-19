package id.ac.ui.cs.advprog.papikos.paymentMain.controller;

import id.ac.ui.cs.advprog.papikos.paymentMain.service.PaymentService;
import id.ac.ui.cs.advprog.papikos.paymentMain.payload.request.PaymentRequest;
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
        return result ? "payment successful" : "payment failed or insufficient balance";
    }
}
