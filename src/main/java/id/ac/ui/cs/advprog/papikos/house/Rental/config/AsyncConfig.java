package id.ac.ui.cs.advprog.papikos.house.Rental.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);             // Jumlah thread minimum
        executor.setMaxPoolSize(10);             // Jumlah thread maksimum
        executor.setQueueCapacity(500);          // Queue sebelum buat thread baru
        executor.setThreadNamePrefix("RentalAsync-"); // Nama prefix thread (buat debugging gampang)
        executor.initialize();
        return executor;
    }
}
