package id.ac.ui.cs.advprog.papikos.paymentmain.service;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import id.ac.ui.cs.advprog.papikos.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.papikos.house.rental.model.Rental;
import id.ac.ui.cs.advprog.papikos.house.rental.repository.RentalRepository;
import id.ac.ui.cs.advprog.papikos.house.rental.service.RentalService;
import id.ac.ui.cs.advprog.papikos.paymentmain.payment.BankTransferPayment;
import id.ac.ui.cs.advprog.papikos.paymentmain.payment.PaymentContext;
import id.ac.ui.cs.advprog.papikos.paymentmain.payment.PaymentStrategy;
import id.ac.ui.cs.advprog.papikos.paymentmain.payment.VirtualAccountPayment;
import id.ac.ui.cs.advprog.papikos.paymentmain.payload.request.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentContext context = new PaymentContext();
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final RentalRepository rentalRepository;
    private final RentalService rentalService;

    public PaymentService(UserRepository userRepository,
                          TransactionService transactionService,
                          RentalRepository rentalRepository,
                          RentalService rentalService) {
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.rentalRepository = rentalRepository;
        this.rentalService = rentalService;
    }

    public boolean handlePayment(PaymentRequest request) {
        try {
            User tenant = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));

            Rental rental = rentalRepository.findById(request.getRentalId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found"));
            rental.setPaid(true);
            rentalRepository.save(rental);

            // ✅ Invalidate rental cache
            rentalService.updateRentalCache(rental);

            User landlord = userRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Landlord not found"));

            if (tenant.getBalance() < request.getAmount()) {
                return false;
            }

            PaymentStrategy strategy = getPaymentStrategy(request.getMethod());
            context.setStrategy(strategy);
            boolean success = context.executePayment(request.getAmount(), tenant.getBalance());

            if (!success) return false;

            tenant.setBalance(tenant.getBalance() - request.getAmount());
            landlord.setBalance(landlord.getBalance() + request.getAmount());

            userRepository.save(tenant);
            userRepository.save(landlord);

            transactionService.createTransaction(
                    tenant.getId(), request.getAmount(), request.getMethod(), "RENT_PAYMENT"
            );

            return true;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Payment failed", e);
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
                tenant,
                landlord,
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
            case "fail" -> amount -> false; // test-only strategy for coverage
            default -> throw new IllegalArgumentException("Unsupported payment method: " + method);
        };
    }
}
