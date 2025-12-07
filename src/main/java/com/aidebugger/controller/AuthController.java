package com.aidebugger.controller;

import com.aidebugger.dto.AuthRequest;
import com.aidebugger.dto.AuthResponse;
import com.aidebugger.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // ========== HTML PAGES ==========
    @GetMapping("/")
    public String index() { return "index.html"; }

    @GetMapping("/index")
    public String index2() { return "index.html"; }

    @GetMapping("/login")
    public String loginPage() { return "login.html"; }

    @GetMapping("/register")
    public String registerPage() { return "register.html"; }

    @GetMapping("/dashboard")
    public String dashboard() { return "dashboard.html"; }

    // ========== API ENDPOINTS ==========
    // MUST return JSON, not HTML!
    
    @PostMapping("/api/auth/register")
    @ResponseBody  // ← CRITICAL: This ensures JSON response
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest req) {
        try {
            return ResponseEntity.ok(authService.register(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse("ERROR: " + e.getMessage()));
        }
    }

    @PostMapping("/api/auth/login")
    @ResponseBody  // ← CRITICAL: This ensures JSON response
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        try {
            return ResponseEntity.ok(authService.login(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse("ERROR: " + e.getMessage()));
        }
    }

    @PostMapping("/api/auth/logout")
    @ResponseBody  // ← CRITICAL: This ensures JSON response
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            authService.logout(token);
        }
        return ResponseEntity.noContent().build();
    }
}