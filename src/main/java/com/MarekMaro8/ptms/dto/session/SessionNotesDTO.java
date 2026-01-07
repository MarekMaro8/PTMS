package com.MarekMaro8.ptms.dto.session;

import jakarta.validation.constraints.Size;

public record SessionNotesDTO(
        @Size(max = 2000, message = "Notatka nie może przekraczać 2000 znaków")
        String notes
) {}
