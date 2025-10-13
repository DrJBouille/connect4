package com.connect4.model.dto

import com.connect4.model.JobParameters
import jakarta.validation.constraints.NotBlank

data class BatchParameters(
  @NotBlank
  val jobParameters: List<JobParameters>,
)
