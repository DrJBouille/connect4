package com.connect4.model.dto

import com.connect4.model.Parameters
import jakarta.validation.constraints.NotBlank

data class BatchParameters(
  @NotBlank
  val parameters: List<Parameters>,
)
