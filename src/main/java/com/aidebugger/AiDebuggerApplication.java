package com.aidebugger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AiDebuggerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiDebuggerApplication.class, args);
    }
}