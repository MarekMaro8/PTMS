package com.MarekMaro8.ptms.dto.client;

import com.MarekMaro8.ptms.model.Client;
import jakarta.validation.constraints.NotNull;

public record HealthUpdateDTO(
        @NotNull(message = "Health status cannot be null")
        Client.HealthStatus status
) {}