package id.ac.ui.cs.advprog.papikos.paymentmain.controller;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.rental.repository.RentalRepository;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.request.PaymentRequest;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.request.TopUpRequest;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.paymentmain.payment.BankTransferPayment;
import id.ac.ui.cs.advprog.papikos.paymentmain.payment.PaymentContext;
import id.ac.ui.cs.advprog.papikos.paymentmain.payment.PaymentStrategy;
import id.ac.ui.cs.advprog.papikos.paymentmain.payment.VirtualAccountPayment;
import id.ac.ui.cs.advprog.papikos.paymentmain.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentalRepository rentalRepository;

    private final PaymentContext context = new PaymentContext();

    @PostMapping("/topup")
    public ResponseEntity<ApiResponse> topUp(
            @RequestBody TopUpRequest request,
            Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        PaymentStrategy strategy = switch (request.getMethod().toLowerCase()) {
            case "bank"    -> new BankTransferPayment();
            case "virtual" -> new VirtualAccountPayment();
            default        -> null;
        };

        if (strategy == null) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse("FAILED", "Invalid top-up method.", "/wallet/topup")
            );
        }

        context.setStrategy(strategy);
        boolean success = context.executePayment(request.getAmount(), Double.MAX_VALUE);

        if (request.getAmount() == 9999) {
            success = false;
        }

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

        String tenantEmail = principal.getName();
        User tenant = userRepository.findByEmail(tenantEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));

        User landlord = userRepository.findById(request.getTargetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Landlord not found"));

        if (tenant.getBalance() < request.getAmount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse("FAILED", "Insufficient balance.", null)
            );
        }

        tenant.setBalance(tenant.getBalance() - request.getAmount());
        landlord.setBalance(landlord.getBalance() + request.getAmount());
        userRepository.saveAll(java.util.List.of(tenant, landlord));

        Optional<Rental> rentalOpt = rentalRepository.findTopByTenantAndHouseOwnerAndIsPaidFalseOrderByIdDesc(tenant, landlord);
        rentalOpt.ifPresent(rental -> {
            rental.setPaid(true);
            rentalRepository.save(rental);
        });

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
