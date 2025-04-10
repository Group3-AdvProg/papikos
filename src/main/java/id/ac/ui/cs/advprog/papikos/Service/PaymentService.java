package com.group3.papikos.service;

import com.group3.papikos.payment.*;
import com.group3.papikos.payload.request.PaymentRequest;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentContext context = new PaymentContext();

    public boolean handlePayment(PaymentRequest request) {
        PaymentStrategy strategy = switch (request.getMethod().toLowerCase()) {
            case "bank" -> new BankTransferPayment();
            case "virtual" -> new VirtualAccountPayment();
            default -> null;
        };

        if (strategy == null) {
            System.out.println("Invalid payment method: " + request.getMethod());
            return false;
        }

        context.setStrategy(strategy);
        return context.executePayment(request.getAmount(), request.getBalance());
    }
}
