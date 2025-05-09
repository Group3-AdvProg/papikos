package id.ac.ui.cs.advprog.papikos.controller;

import id.ac.ui.cs.advprog.papikos.model.Customer;
import id.ac.ui.cs.advprog.papikos.repository.CustomerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Customer> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Customer add(@RequestBody Customer customer) {
        return repository.save(customer);
    }
}
