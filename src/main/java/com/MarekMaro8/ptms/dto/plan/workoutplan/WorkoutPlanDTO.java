package com.MarekMaro8.ptms.dto.plan.workoutplan;

import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayDTO;

import java.util.List;

public record WorkoutPlanDTO(
        Long id,
        String name,
        String description,
        boolean isActive,
        Long clientId,
        List<WorkoutDayDTO> workoutDays
) {}