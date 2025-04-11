package id.ac.ui.cs.advprog.papikos.Controller;

import id.ac.ui.cs.advprog.papikos.Service.PaymentService;
import id.ac.ui.cs.advprog.papikos.payload.Request.PaymentRequest;
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
