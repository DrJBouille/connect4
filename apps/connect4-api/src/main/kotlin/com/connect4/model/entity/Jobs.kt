package com.connect4.model.entity

import com.connect4.model.Parameters
import com.connect4.model.Stats
import com.connect4.model.Status

class Jobs {
  lateinit var jobsId: String
  lateinit var batchId: String
  lateinit var parameters: Parameters
  var stats: Stats? = null
  var status: Status = Status.NOT_STARTED
}
