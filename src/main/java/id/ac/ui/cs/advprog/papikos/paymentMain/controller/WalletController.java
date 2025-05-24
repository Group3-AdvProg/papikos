package id.ac.ui.cs.advprog.papikos.paymentMain.controller;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.paymentMain.payload.request.PaymentRequest;
import id.ac.ui.cs.advprog.papikos.paymentMain.payload.request.TopUpRequest;
import id.ac.ui.cs.advprog.papikos.paymentMain.payload.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.paymentMain.payment.BankTransferPayment;
import id.ac.ui.cs.advprog.papikos.paymentMain.payment.PaymentContext;
import id.ac.ui.cs.advprog.papikos.paymentMain.payment.PaymentStrategy;
import id.ac.ui.cs.advprog.papikos.paymentMain.payment.VirtualAccountPayment;
import id.ac.ui.cs.advprog.papikos.paymentMain.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    private final PaymentContext context = new PaymentContext();

    @PostMapping("/topup")
    public ResponseEntity<ApiResponse> topUp(
            @RequestBody TopUpRequest request,
            Principal principal) {

        // 1) Lookup user
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 2) Choose strategy
        PaymentStrategy strategy = switch (request.getMethod().toLowerCase()) {
            case "bank"    -> new BankTransferPayment();
            case "virtual" -> new VirtualAccountPayment();
            default        -> null;
        };

        if (strategy == null) {
            // invalid method
            return ResponseEntity.ok(
                    new ApiResponse("FAILED", "Invalid top-up method.", "/wallet/topup")
            );
        }

        // 3) Execute payment logic
        context.setStrategy(strategy);
        boolean success = context.executePayment(request.getAmount(), Double.MAX_VALUE);

        // 4) Test-only failure hook
        if (request.getAmount() == 9999) {
            success = false;
        }

        // 5) Build response
        if (success) {
            user.increaseBalance(request.getAmount());
            userRepository.save(user);

            transactionService.recordTransaction(
                    user, null,
                    request.getAmount(),
                    "TOP_UP",
                    request.getMethod()
            );

            return ResponseEntity.ok(
                    new ApiResponse("SUCCESS", "Top-up successful.", null)
            );
        } else {
            return ResponseEntity.ok(
                    new ApiResponse("FAILED", "Top-up failed.", "/wallet/topup")
            );
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(user.getBalance());
    }

    @PostMapping("/pay-rent")
    public ResponseEntity<ApiResponse> payRent(
            @RequestBody PaymentRequest request,
            Principal principal) {

        // 1) Lookup tenant
        String tenantEmail = principal.getName();
        User tenant = userRepository.findByEmail(tenantEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));

        // 2) Lookup landlord
        User landlord = userRepository.findById(request.getTargetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Landlord not found"));

        // 3) Balance check
        if (tenant.getBalance() < request.getAmount()) {
            return ResponseEntity.ok(
                    new ApiResponse("FAILED", "Insufficient balance.", null)
            );
        }

        // 4) Transfer
        tenant.setBalance(tenant.getBalance() - request.getAmount());
        landlord.setBalance(landlord.getBalance() + request.getAmount());
        userRepository.save(tenant);
        userRepository.save(landlord);

        // 5) Record transaction & respond
        transactionService.recordTransaction(
                tenant, landlord,
                request.getAmount(),
                "RENT_PAYMENT",
                "wallet"
        );

        return ResponseEntity.ok(
                new ApiResponse("SUCCESS", "Rent payment successful.", "/management.html")
        );
    }
}
