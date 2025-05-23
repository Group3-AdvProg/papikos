// src/main/java/id/ac/ui/cs/advprog/papikos/chat/config/AsyncConfig.java
package id.ac.ui.cs.advprog.papikos.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Defines the Executor for @Async methods.
     * - Core pool of 4 threads, can grow to 10 under load.
     * - Queue up to 500 tasks before rejecting.
     * - Threads named "AsyncExecutor-#".
     */
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }
}
