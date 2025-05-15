package id.ac.ui.cs.advprog.papikos.controller;

import id.ac.ui.cs.advprog.papikos.payload.request.TopUpRequest;
import id.ac.ui.cs.advprog.papikos.payment.*;
import id.ac.ui.cs.advprog.papikos.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private TransactionService transactionService;

    private final PaymentContext context = new PaymentContext();

    @PostMapping("/topup")
    public String topUp(@RequestBody TopUpRequest request) {
        PaymentStrategy strategy = switch (request.getMethod().toLowerCase()) {
            case "bank" -> new BankTransferPayment();
            case "virtual" -> new VirtualAccountPayment();
            default -> null;
        };

        if (strategy == null) {
            return "Invalid top-up method.";
        }

        context.setStrategy(strategy);

        // No need to check balance for top-up
        boolean success = context.executePayment(request.getAmount(), Double.MAX_VALUE);

        if (success) {
            transactionService.recordTransaction(
                    request.getUserId(),
                    null,
                    request.getAmount(),
                    "TOP_UP",
                    request.getMethod()
            );
            return "Top-up successful.";
        }

        return "Top-up failed.";
    }
}
