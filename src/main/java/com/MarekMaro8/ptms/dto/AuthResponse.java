package com.MarekMaro8.ptms.dto;

public class AuthResponse {
    private String token;
    private String role; // Przydatne dla Frontendu (np. żeby pokazać panel Trenera)
    // Opcjonalnie możesz tu dodać id, firstName itp.

    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getRole() { return role; }
}