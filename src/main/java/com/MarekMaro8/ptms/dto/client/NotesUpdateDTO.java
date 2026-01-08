package com.MarekMaro8.ptms.dto.client;

import jakarta.validation.constraints.Size;

public record NotesUpdateDTO(
        @Size(max = 2000, message = "Notatka nie może przekraczać 2000 znaków")
        String notes
) {}