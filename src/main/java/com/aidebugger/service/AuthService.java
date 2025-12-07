package com.aidebugger.service;

import com.aidebugger.dto.AuthRequest;
import com.aidebugger.dto.AuthResponse;
import com.aidebugger.entity.AppUser;
import com.aidebugger.repository.UserRepository;
import com.aidebugger.security.JwtUtil;
import com.aidebugger.security.RedisTokenBlacklistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTokenBlacklistService blacklistService;

    @Transactional
    public AuthResponse register(AuthRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username '" + req.getUsername() + "' already exists");
        }

        AppUser user = AppUser.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .role("USER")
                .build();
        
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest req) {
        AppUser user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    public void logout(String token) {
        long ttl = jwtUtil.getExpirationMillis(token);
        blacklistService.blacklistToken(token, ttl);
    }
}