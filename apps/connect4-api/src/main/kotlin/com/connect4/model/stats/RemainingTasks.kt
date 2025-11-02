package com.connect4.model.stats

data class RemainingTasks (
  val threadInUse: Int = 0,
  val totalThread: Int = 0,
  val remainingBatches: Int = 0,
  val notStartedJobs: Int = 0,
  val runningJobs: Int = 0,
  val finishedJobs: Int = 0
)
