package com.MarekMaro8.ptms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainers")
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
    private Set<Client> clients = new HashSet<>();

    public Trainer() {
    }

    public Trainer(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public void addClient(Client client) {
        this.clients.add(client);   // 1. Dodaj Klienta do listy trenera
        client.setTrainer(this);    // 2. Ustaw Trenera jako właściciela w klasie Klient (USTAWIA Foreign Key!)
    }

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

    public Set<Client> getClients() {
        return clients;
    }

    // ... (Konstruktory, Gettery i Settery) ...
    // Dodaj Gettery/Settery dla 'clients'
}