package com.MarekMaro8.ptms.dto.trainer;

public class TrainerDTO {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;

    public TrainerDTO(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }



    public String getFirstName() {
        return firstName;
    }

    public Long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}