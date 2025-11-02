package com.connect4.model.jobs

import com.connect4.model.Parameters
import jakarta.validation.constraints.NotBlank

data class JobsParameters(
    @NotBlank
  val parameters: List<Parameters>,
)
