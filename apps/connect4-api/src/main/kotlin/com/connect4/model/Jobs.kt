package com.connect4.model

class Jobs {
  lateinit var jobsId: String
  lateinit var batchId: String
  lateinit var parameters: JobParameters
  var stats: Stats? = null
  var status: Status = Status.NOT_STARTED
}
