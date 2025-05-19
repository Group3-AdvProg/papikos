package id.ac.ui.cs.advprog.papikos.service;

import id.ac.ui.cs.advprog.papikos.model.User;
import id.ac.ui.cs.advprog.papikos.payment.BankTransferPayment;
import id.ac.ui.cs.advprog.papikos.payment.PaymentContext;
import id.ac.ui.cs.advprog.papikos.payment.PaymentStrategy;
import id.ac.ui.cs.advprog.papikos.payment.VirtualAccountPayment;
import id.ac.ui.cs.advprog.papikos.payload.request.PaymentRequest;
import id.ac.ui.cs.advprog.papikos.repository.UserRepository;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentContext context = new PaymentContext();
    @Setter
    private UserRepository userRepository;
    @Setter
    private TransactionService transactionService;

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

    public String handleRentPayment(String tenantId, String landlordId, double rentAmount) {
        User tenant = userRepository.findById(Long.parseLong(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

        User landlord = userRepository.findById(Long.parseLong(landlordId))
                .orElseThrow(() -> new IllegalArgumentException("Landlord not found"));

        if (tenant.getBalance() < rentAmount) {
            return "INSUFFICIENT";
        }

        tenant.decreaseBalance(rentAmount);
        landlord.increaseBalance(rentAmount);

        userRepository.save(tenant);
        userRepository.save(landlord);

        transactionService.recordTransaction(
                tenantId,
                landlordId,
                rentAmount,
                "RENT_PAYMENT",
                "wallet"
        );

        return "SUCCESS";
    }

}

