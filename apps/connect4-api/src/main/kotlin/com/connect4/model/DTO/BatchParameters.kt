package com.connect4.model.DTO

import com.connect4.model.JobParameter
import jakarta.validation.constraints.NotBlank

data class BatchParameters(
  @NotBlank
  val nbOfProcess: Int = 1,
  val jobParameter: JobParameter = JobParameter(),
)
