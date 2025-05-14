package id.ac.ui.cs.advprog.papikos.service;

import id.ac.ui.cs.advprog.papikos.payment.BankTransferPayment;
import id.ac.ui.cs.advprog.papikos.payment.PaymentContext;
import id.ac.ui.cs.advprog.papikos.payment.PaymentStrategy;
import id.ac.ui.cs.advprog.papikos.payment.VirtualAccountPayment;
import id.ac.ui.cs.advprog.papikos.payload.request.PaymentRequest;
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
