package com.MarekMaro8.ptms.dto.session;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record SessionStartDTO(
        String notes,

        @Min(1) @Max(10)
        Integer energyLevel,

        @Min(1) @Max(10)
        Integer sleepQuality,

        @Min(1) @Max(10)
        Integer stressLevel,

        Double bodyWeight
) {}