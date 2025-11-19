package com.MarekMaro8.ptms.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName; // Imię klienta

    private String lastName;  // Nazwisko klienta

    @Column(nullable = false, unique = true)
    private String email; // Adres e-mail (musi być unikalny)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(nullable = false)
    private String password; // Hasło



    // --- Konstruktory (wymagane przez JPA/Hibernate) ---

    // Domyślny konstruktor bez argumentów jest wymagany przez JPA
    public Client() {}

    public Client(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // --- Gettery i Settery (wymagane do odczytu i zapisu pól) ---

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}