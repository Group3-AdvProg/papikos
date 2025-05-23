package id.ac.ui.cs.advprog.papikos.paymentMain.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
public class LoggingController {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LoggingController.class);

    @PostMapping
    public void logClientError(@RequestBody String message) {
        logger.error("Client error: {}", message);
    }
}