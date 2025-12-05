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
                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/api/trainer/*/clients/**",
                                "/api/trainer/*/assign/*").permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/**").permitAll()

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/trainer/*/unassign/*").permitAll()

                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
