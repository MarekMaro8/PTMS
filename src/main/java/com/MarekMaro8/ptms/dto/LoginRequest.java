package com.MarekMaro8.ptms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Email(message = "Invalid email format")
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
) {}