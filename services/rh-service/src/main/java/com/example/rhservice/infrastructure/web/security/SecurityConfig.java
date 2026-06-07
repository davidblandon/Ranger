package com.example.rhservice.infrastructure.web.security;

import com.example.rhservice.infrastructure.web.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) -> writeErrorResponse(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized",
                "Authentication is required to access this resource.",
                request,
                objectMapper,
                authException
            ))
            .accessDeniedHandler((request, response, accessDeniedException) -> writeErrorResponse(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                "You do not have permission to access this resource.",
                request,
                objectMapper,
                accessDeniedException
            ))
        )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/login"), AntPathRequestMatcher.antMatcher("/api/auth/logout")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**"), AntPathRequestMatcher.antMatcher("/v3/api-docs/**"), AntPathRequestMatcher.antMatcher("/swagger-ui.html"), AntPathRequestMatcher.antMatcher("/h2-console/**"), AntPathRequestMatcher.antMatcher("/actuator/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.GET, "/api/admins/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.POST, "/api/admins/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.PUT, "/api/admins/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.DELETE, "/api/admins/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.GET, "/api/employees")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.POST, "/api/employees/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.PUT, "/api/employees/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.DELETE, "/api/employees/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.POST, "/api/payrolls/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.PUT, "/api/payrolls/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.DELETE, "/api/payrolls/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.POST, "/api/shifts/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.PUT, "/api/shifts/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.DELETE, "/api/shifts/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/**")).authenticated()
                        .anyRequest().denyAll())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            int status,
            String error,
            String message,
            HttpServletRequest request,
            ObjectMapper objectMapper,
            Exception exception
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                request.getRequestURI(),
                Map.of("exception", exception.getClass().getSimpleName())
        );

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}