package com.MarekMaro8.ptms.dto.session;


public record SessionSetDTO(
     Long id,
     Integer setNumber,
     Integer reps,
     Double weight,
     Double rpe
) {}


