package id.ac.ui.cs.advprog.papikos.paymentMain.service;

import id.ac.ui.cs.advprog.papikos.paymentMain.model.User;
import id.ac.ui.cs.advprog.papikos.paymentMain.payment.BankTransferPayment;
import id.ac.ui.cs.advprog.papikos.paymentMain.payment.PaymentContext;
import id.ac.ui.cs.advprog.papikos.paymentMain.payment.PaymentStrategy;
import id.ac.ui.cs.advprog.papikos.paymentMain.payment.VirtualAccountPayment;
import id.ac.ui.cs.advprog.papikos.paymentMain.payload.request.PaymentRequest;
import id.ac.ui.cs.advprog.papikos.paymentMain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentContext context = new PaymentContext();
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public PaymentService(UserRepository userRepository, TransactionService transactionService) {
        this.userRepository = userRepository;
        this.transactionService = transactionService;
    }

    public boolean handlePayment(PaymentRequest request) {
        try {
            PaymentStrategy strategy = getPaymentStrategy(request.getMethod());
            context.setStrategy(strategy);
            return context.executePayment(request.getAmount(), request.getBalance());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid payment method received: {}", request.getMethod());
            return false;
        }
    }

    public String handleRentPayment(String tenantId, String landlordId, double rentAmount) {
        User tenant = userRepository.findById(Long.parseLong(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

        User landlord = userRepository.findById(Long.parseLong(landlordId))
                .orElseThrow(() -> new IllegalArgumentException("Landlord not found"));

        if (tenant.getBalance() < rentAmount) {
            logger.info("Tenant {} has insufficient balance: {} < {}", tenantId, tenant.getBalance(), rentAmount);
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

        logger.info("Rent payment successful from tenant {} to landlord {} for amount {}", tenantId, landlordId, rentAmount);
        return "SUCCESS";
    }

    private PaymentStrategy getPaymentStrategy(String method) {
        return switch (method.toLowerCase()) {
            case "bank" -> new BankTransferPayment();
            case "virtual" -> new VirtualAccountPayment();
            default -> throw new IllegalArgumentException("Unsupported payment method: " + method);
        };
    }
}
