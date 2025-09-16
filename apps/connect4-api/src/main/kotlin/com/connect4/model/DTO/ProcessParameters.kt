package com.connect4.model.DTO

import com.connect4.model.BotsParameters
import jakarta.validation.constraints.NotBlank

data class ProcessParameters(
  @NotBlank
  val nbOfProcess: Int = 1,
  val botsParameters: BotsParameters = BotsParameters(),
)
