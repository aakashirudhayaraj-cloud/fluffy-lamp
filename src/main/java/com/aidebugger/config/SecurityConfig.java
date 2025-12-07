package com.aidebugger.config;

import com.aidebugger.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF for REST API calls
                .csrf(csrf -> csrf.disable())
                
                // Enable CORS (uses CorsConfig.java)
                .cors(Customizer.withDefaults())

                // Allow these endpoints WITHOUT authentication
                .authorizeHttpRequests(auth -> auth
                        // Static resources and HTML pages
                        .requestMatchers(HttpMethod.GET,
                                "/", "/index",
                                "/login", "/register", "/dashboard",
                                "/test-api.html",
                                "/css/**", "/js/**", "/images/**",
                                "/error", "/error/**"
                        ).permitAll()
                        
                        // Allow POST to auth endpoints
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/**"
                        ).permitAll()
                        
                        // Allow OPTIONS for CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Everything else requires authentication
                        .anyRequest().permitAll()
                )

                // No session (JWT only)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Handle unauthorized access
                .exceptionHandling(exception -> 
                    exception.authenticationEntryPoint((request, response, authException) -> {
                        System.out.println("Unauthorized access to: " + request.getRequestURI());
                        
                        // If API call, return 401 JSON
                        if (request.getRequestURI().startsWith("/api/")) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"error\":\"Unauthorized - Please login\"}");
                        } else {
                            // For web pages, redirect to login
                            response.sendRedirect("/login");
                        }
                    })
                );

        // Add JWT filter BEFORE UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}