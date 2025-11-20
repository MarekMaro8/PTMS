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
                // Wyłącz zabezpieczenia CSRF, ponieważ używamy JSON API
                .csrf(AbstractHttpConfigurer::disable)

                // Konfiguracja autoryzacji żądań HTTP
                .authorizeHttpRequests(authorize -> authorize

                        // ZEZWÓL na żądania POST do /api/clients (rejestracja)
                        .requestMatchers(HttpMethod.POST, "/api/clients").permitAll()

                        // ZEZWÓL na żądania GET do /api/clients (na razie dla testów)
                        .requestMatchers(HttpMethod.GET, "/api/clients").permitAll()
                        // ZEZWÓL na żądania POST do /api/clients/login (logowanie)
                        .requestMatchers(HttpMethod.POST, "/api/clients/login").permitAll()

                        // WSZYSTKIE inne żądania wymagają pełnej autoryzacji
                        .anyRequest().authenticated()
                );

        // Możesz usunąć domyślny formularz logowania, który Spring Security dodaje
        // http.httpBasic(Customizer.withDefaults()); // Lub użyć httpBasic

        return http.build();
    }
}

