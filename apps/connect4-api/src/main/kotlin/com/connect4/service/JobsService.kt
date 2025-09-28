package com.connect4.service

import com.connect4.controller.BatchesNotifier
import com.connect4.controller.JobsNotifier
import com.connect4.model.Status
import com.connect4.model.Batch
import com.connect4.model.Jobs
import com.connect4.model.RemainingTasks
import com.connect4.repository.BatchesRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicBoolean

@ApplicationScoped
class JobsService {

  @Inject private lateinit var batchesRepository: BatchesRepository

  fun getJob(batchId: String, jobId: String): Jobs? {
    val batch = batchesRepository.finByBatchId(batchId) ?: return null
    return batch.jobs.find { it.jobsId == jobId }
  }

  fun getJobs(batchId: String): List<Jobs> {
    val batch = batchesRepository.finByBatchId(batchId) ?: return emptyList()
    return batch.jobs
  }

  fun getAllJobs(): List<Jobs> {
    val batches = batchesRepository.listAll()
    return batches.flatMap { it.jobs }
  }
}
