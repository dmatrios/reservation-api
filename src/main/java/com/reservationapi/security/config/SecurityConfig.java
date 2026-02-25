// src/main/java/com/reservationapi/security/config/SecurityConfig.java
package com.reservationapi.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservationapi.security.jwt.JwtAuthFilter;
import com.reservationapi.security.service.CustomUserDetailsService;
import com.reservationapi.shared.exceptions.ErrorCode;
import com.reservationapi.shared.web.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.Instant;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // ✅ habilita CORS (usa CorsConfigurationSource bean)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // ✅ preflight
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            writeError(
                                    request,
                                    response,
                                    401,
                                    ErrorCode.UNAUTHORIZED.name(),
                                    "Authentication required or token invalid"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            writeError(
                                    request,
                                    response,
                                    403,
                                    ErrorCode.FORBIDDEN.name(),
                                    "You are not authorized to access this resource"
                            );
                        })
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void writeError(
            HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            int status,
            String errorCode,
            String message
    ) throws java.io.IOException {

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .path(request.getRequestURI())
                .build();

        response.setStatus(status);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), body);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }
}