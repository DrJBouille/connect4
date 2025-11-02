package com.connect4.model.tasks

import com.connect4.model.Parameters
import com.connect4.model.Status
import com.connect4.model.stats.Stats

class Tasks {
  lateinit var id: String
  lateinit var jobId: String
  lateinit var parameters: Parameters
  var stats: Stats? = null
  var status: Status = Status.NOT_STARTED
}
