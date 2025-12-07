package com.aidebugger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Value("${app.async.core-pool-size:4}")
    private int corePool;
    @Value("${app.async.max-pool-size:8}")
    private int maxPool;

    @Bean(name = "aiExecutor")
    public Executor aiExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(corePool);
        exec.setMaxPoolSize(maxPool);
        exec.setQueueCapacity(200);
        exec.setThreadNamePrefix("AI-Exec-");
        exec.initialize();
        return exec;
    }
}
