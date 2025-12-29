package com.MarekMaro8.ptms.dto;

public class AuthResponse {
    private String token;
    private String role;

    // NOWE POLA: To jest to, czego brakowało Twojemu Frontendowi
    private Long id;
    private String firstName;
    private String lastName;

    // ZMODYFIKOWANY KONSTRUKTOR: Musi przyjmować teraz 4 argumenty zamiast 2
    public AuthResponse(String token, String role, Long id, String firstName, String lastName) {
        this.token = token;
        this.role = role;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // GETTERY: Niezbędne, aby Jackson (biblioteka JSON) mógł "wyjąć" te dane i wysłać je w świat
    public String getToken() { return token; }
    public String getRole() { return role; }
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }

    public String getLastName() {
        return lastName;
    }
}