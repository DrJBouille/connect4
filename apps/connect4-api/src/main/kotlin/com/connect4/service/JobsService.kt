package com.connect4.service

import com.connect4.controller.JobsResultsNotifier
import com.connect4.model.JobParameter
import com.connect4.model.ContainerStatus
import com.connect4.model.DTO.BatchParameters
import com.connect4.model.JobBatch
import com.connect4.model.Jobs
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.LinkedList
import java.util.Queue
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@ApplicationScoped
class JobsService {
  @Inject
  private lateinit var dockerService: DockerService

  @Inject
  private lateinit var jobsResultsNotifier: JobsResultsNotifier

  private val batchQueue: Queue<JobBatch> = LinkedList()
  private val jobsResults = mutableMapOf<String, Jobs>()

  private val cores = Runtime.getRuntime().availableProcessors()
  private val executor = Executors.newFixedThreadPool(cores - 3)

  private val isRunning = AtomicBoolean(false)

  fun startProcess() {
    if (isRunning.get()) return

    isRunning.set(true)
    val batchWorker = Thread {
      while(true) {
        val batch = batchQueue.poll()

        if (batch == null) {
          isRunning.set(false)
          break
        }

        batch.jobs.forEach {
          it.containerStatus = ContainerStatus.PENDING
          jobsResults[it.jobsId] = it
          jobsResultsNotifier.broadcast(it.jobsId)
        }

        val futures = batch.jobs.map { job ->
          executor.submit {
            try {
              job.containerStatus = ContainerStatus.STARTED
              jobsResults[job.jobsId] = job
              jobsResultsNotifier.broadcast(job.jobsId)

              val stats = dockerService.getStats(job.jobParameter)
              job.stats = stats

              jobsResults[job.jobsId] = job

              job.containerStatus = ContainerStatus.FINISHED
            } catch (e: Exception) {
              job.containerStatus = ContainerStatus.FAILED
            }
            jobsResultsNotifier.broadcast(job.jobsId)
          }
        }

        futures.forEach { it.get() }
      }
    }

    batchWorker.isDaemon = true
    batchWorker.start()
  }

  fun addBatch(batchParameters: BatchParameters): String {
    val batchId = UUID.randomUUID().toString()
    val jobs = List(batchParameters.nbOfProcess) { Jobs(UUID.randomUUID().toString(), batchId, batchParameters.jobParameter) }

    jobs.forEach {
      jobsResults[it.jobsId] = it
      jobsResultsNotifier.broadcast(it.jobsId)
    }
    batchQueue.add(JobBatch(batchId, jobs))
    return batchId
  }

  fun getJob(id: String): Jobs? {
    return jobsResults[id]
  }
}
