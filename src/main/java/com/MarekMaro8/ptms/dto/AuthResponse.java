package com.MarekMaro8.ptms.dto;

public record AuthResponse(
        String token,
        String role,
        Long id,
        String firstName,
        String lastName
) {}