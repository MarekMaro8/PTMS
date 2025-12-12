package com.MarekMaro8.ptms.dto.session;

public class SessionNotesDTO {
    private String notes;

    public SessionNotesDTO() {}

    // Konstruktor dla wygody testów
    public SessionNotesDTO(String notes) {
        this.notes = notes;
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}