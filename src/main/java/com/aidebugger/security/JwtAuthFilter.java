package com.aidebugger.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RedisTokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Skip JWT filter for login/register endpoints and static resources
        if (path.startsWith("/api/auth/") || 
            path.equals("/login") || path.equals("/register") || 
            path.equals("/") || path.equals("/index") ||
            path.startsWith("/css/") || path.startsWith("/js/") || 
            path.startsWith("/images/") || path.startsWith("/error")) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            // No token, continue (will be caught by Spring Security if endpoint requires auth)
            chain.doFilter(request, response);
            return;
        }
        
        String token = header.substring(7);
        
        // Check if token is valid and not blacklisted
        if (!jwtUtil.validateToken(token) || blacklistService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Token is valid, set authentication
        String username = jwtUtil.extractUsername(token);
        Authentication auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        chain.doFilter(request, response);
    }
}