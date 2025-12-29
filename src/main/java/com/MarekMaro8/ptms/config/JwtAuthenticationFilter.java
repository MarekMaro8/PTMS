package com.MarekMaro8.ptms.config;

import com.MarekMaro8.ptms.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Sprawdzamy nagłówek "Authorization"
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Jeśli nie ma nagłówka lub nie zaczyna się od "Bearer ", puszczamy dalej (może to zapytanie publiczne np. login)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Wyciągamy token (ucięcie "Bearer " czyli pierwszych 7 znaków)
        jwt = authHeader.substring(7);

        // 3. Wyciągamy email z tokena
        userEmail = jwtService.extractUsername(jwt);

        // 4. Jeśli mamy email, a użytkownik nie jest jeszcze zalogowany w kontekście Springa...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // ... pobieramy dane użytkownika z bazy
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 5. Sprawdzamy czy token jest ważny
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 6. Jeśli tak - tworzymy obiekt autoryzacji i wkładamy go do "Szuflady" (SecurityContext)
                // Dzięki temu w Kontrolerze będziesz mógł używać "Principal principal"
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Idziemy dalej do kontrolera
        filterChain.doFilter(request, response);
    }
}