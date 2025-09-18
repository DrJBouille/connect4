package com.connect4.model

data class Jobs(
  val jobsId: String,
  val batchId: String,
  val jobParameter: JobParameter,
  var stats: Stats? = null,
  var containerStatus: ContainerStatus = ContainerStatus.NOT_STARTED,
)
