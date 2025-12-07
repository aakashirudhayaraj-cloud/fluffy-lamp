package com.aidebugger.controller;

import com.aidebugger.dto.DebugRequest;
import com.aidebugger.service.DebugService;
import com.aidebugger.service.OpenAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {
    private final DebugService debugService;
    private final OpenAiService openAiService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@Valid @RequestBody DebugRequest request, 
                                   BindingResult bindingResult) {
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Validation failed: " + errors);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            System.out.println("\n=== 📨 DEBUG REQUEST RECEIVED ===");
            System.out.println("👤 Language: " + request.getLanguage());
            System.out.println("📝 Code length: " + request.getCodeSnippet().length());
            System.out.println("📋 Context: " + request.getContext());
            System.out.println("⏱️  Timestamp: " + new Date());
            
            // Call the service asynchronously
            String answer = debugService.analyzeAsync(request).get();
            
            System.out.println("✅ Request processed successfully");
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("timestamp", new Date());
            response.put("language", request.getLanguage());
            response.put("analysis", answer);
            response.put("charactersProcessed", request.getCodeSnippet().length());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error in debug controller: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("error", "Server error: " + e.getMessage());
            error.put("timestamp", new Date());
            error.put("suggestion", "Check OpenAI API key and internet connection");
            
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ========== TEST ENDPOINTS ==========
    
    @GetMapping("/test-ai-connection")
    public ResponseEntity<Map<String, Object>> testAIConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("\n=== 🤖 AI CONNECTION TEST STARTED ===");
            
            // Simple test prompt
            String testPrompt = "You are a helpful assistant for Code Mentor AI. "
                              + "Say exactly: '✅ AI Debugger is connected and working!' "
                              + "Then add one random fact about programming in Java.";
            
            System.out.println("📤 Sending test prompt to OpenAI...");
            String aiResponse = openAiService.askDebugAssistant(testPrompt);
            
            response.put("status", "success");
            response.put("message", "OpenAI connection test completed");
            response.put("aiResponse", aiResponse);
            response.put("timestamp", new Date());
            response.put("service", "Code Mentor AI");
            
            System.out.println("✅ AI Connection Test: SUCCESS");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ AI Connection Test Failed: " + e.getMessage());
            
            response.put("status", "error");
            response.put("message", "OpenAI connection failed");
            response.put("error", e.getMessage());
            response.put("timestamp", new Date());
            response.put("hint", "Check application.properties for openai.api.key");
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/quick-test")
    public ResponseEntity<Map<String, Object>> quickTest() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("\n=== ⚡ QUICK AI TEST ===");
            
            // Create a test debug request
            DebugRequest testRequest = new DebugRequest();
            testRequest.setLanguage("java");
            testRequest.setCodeSnippet("""
                public class QuickTest {
                    public static void main(String[] args) {
                        System.out.println("Hello, AI Debugger!");
                        int x = 10 / 2;
                        System.out.println("Result: " + x);
                    }
                }
                """);
            testRequest.setContext("Quick connection test");
            
            System.out.println("🚀 Processing quick test request...");
            String result = debugService.analyzeAsync(testRequest).get();
            
            response.put("status", "success");
            response.put("message", "Quick test completed");
            response.put("result", result);
            response.put("timestamp", new Date());
            response.put("testType", "Java code analysis");
            
            System.out.println("✅ Quick Test: SUCCESS");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Quick Test Failed: " + e.getMessage());
            
            response.put("status", "error");
            response.put("message", "Quick test failed");
            response.put("error", e.getMessage());
            response.put("timestamp", new Date());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        health.put("status", "UP");
        health.put("service", "Code Mentor AI Debugger");
        health.put("timestamp", new Date());
        health.put("version", "1.0.0");
        health.put("developer", "Aakash B.R");
        health.put("endpoints", Map.of(
            "analyze", "POST /api/debug/analyze",
            "testAI", "GET /api/debug/test-ai-connection",
            "quickTest", "GET /api/debug/quick-test",
            "health", "GET /api/debug/health"
        ));
        
        return ResponseEntity.ok(health);
    }
    
    @PostMapping("/simple-test")
    public ResponseEntity<Map<String, Object>> simpleTest(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String code = body.get("code");
            String language = body.getOrDefault("language", "java");
            
            if (code == null || code.trim().isEmpty()) {
                response.put("status", "error");
                response.put("error", "Code is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            System.out.println("\n=== 🧪 SIMPLE TEST ===");
            System.out.println("Language: " + language);
            System.out.println("Code: " + code.substring(0, Math.min(100, code.length())) + "...");
            
            DebugRequest request = new DebugRequest();
            request.setLanguage(language);
            request.setCodeSnippet(code);
            request.setContext("Simple API test");
            
            String analysis = debugService.analyzeAsync(request).get();
            
            response.put("status", "success");
            response.put("analysis", analysis);
            response.put("inputLength", code.length());
            response.put("timestamp", new Date());
            
            System.out.println("✅ Simple Test: SUCCESS");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Simple Test Error: " + e.getMessage());
            
            response.put("status", "error");
            response.put("error", e.getMessage());
            response.put("timestamp", new Date());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}