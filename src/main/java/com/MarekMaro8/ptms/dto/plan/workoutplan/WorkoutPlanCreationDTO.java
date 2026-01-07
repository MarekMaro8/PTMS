package com.MarekMaro8.ptms.dto.plan.workoutplan;

import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayCreationDTO;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public record WorkoutPlanCreationDTO (

    @NotBlank(message = "Nazwa planu jest wymagana")
     String name,

     String description,

     List<WorkoutDayCreationDTO> workoutDays //toDo sprwadzic czy musi byc na koncu tego = new ArrayList<>()

){}

