package id.ac.ui.cs.advprog.papikos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Defines the Executor for @Async methods.
 * - Core pool of 4 threads, can grow to 10 under load.
 * - Queue up to 500 tasks before rejecting.
 * - Threads named "AsyncExecutor-#".
 */

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // jumlah minimum thread
        executor.setMaxPoolSize(10); // jumlah maksimum thread
        executor.setQueueCapacity(500); // kapasitas antrian task
        executor.setThreadNamePrefix("RentalAsync-"); // prefix nama thread
        executor.initialize(); // inisialisasi executor
        return executor;
    }
}
