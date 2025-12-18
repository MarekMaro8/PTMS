package com.MarekMaro8.ptms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. NAPRAWA CORS (Kluczowe dla Reacta)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // 2. NAPRAWA ŚCIEŻEK (używamy ** zamiast *)
                        // Pozwala na dostęp do /api/auth/client/login, /api/auth/trainer/register itp.
                        .requestMatchers("/api/auth/**").permitAll()

                        // Poprawione literówki (usunięte podwójne ukośniki //) i dodane **
                        .requestMatchers(HttpMethod.POST,
                                "/api/trainer/*/clients/**",
                                "/api/trainer/*/assign/**").permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/**").permitAll()

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/trainer/*/unassign/**").permitAll()


                        .requestMatchers(HttpMethod.POST, "/api/workout-plans/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/workout-plans/**").permitAll()

                        .requestMatchers("/api/clients/*/workouts/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/exercises/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/workout-days/**").permitAll()

                        .anyRequest().authenticated()


                //te dwie linijki sa zeby moc testowac w httpclient
                        // bez tokena (przy uzyciu authorization: basic) mozna je potem usunac
                ).httpBasic(Customizer.withDefaults())
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll
        );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Zmień port, jeśli Twój React działa na innym niż 3000
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}