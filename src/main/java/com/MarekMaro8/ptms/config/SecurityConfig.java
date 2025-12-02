package com.MarekMaro8.ptms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Włącza wsparcie dla bezpieczeństwa Spring
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth//trainer/register",
                                "/api/auth/client/register",
                                "/api/auth/trainer/login",
                                "/api/auth/client/login"

                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/trainer/{trainerId}/assign/{clientId}",
                                "/api/trainer/{trainerId}/unassign/{clientId}"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/trainer/{trainerId}/clients",
                                "/api/clients"
                        ).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
