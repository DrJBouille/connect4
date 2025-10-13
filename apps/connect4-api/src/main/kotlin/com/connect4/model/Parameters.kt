package com.connect4.model

import jakarta.validation.constraints.NotBlank

class Parameters{
  @NotBlank
  var nbOfProcess: Int = 1

  @NotBlank
  var redDeepness: Int = 1

  @NotBlank
  var yellowDeepness: Int = 1

  @NotBlank
  var image: Images = Images.Connect4BotKotlin
}
