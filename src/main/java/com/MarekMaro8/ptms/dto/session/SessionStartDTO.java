package com.MarekMaro8.ptms.dto.session;

public class SessionStartDTO {
    // Jedynym polem, które klient może wysłać na start, są opcjonalne notatki
    private String notes;

    public SessionStartDTO() {
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
