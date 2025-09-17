package com.connect4.model

data class Jobs(
  val id: String,
  val jobParameter: JobParameter,
  var stats: Stats? = null,
  var containerStatus: ContainerStatus = ContainerStatus.NOT_STARTED,
)
