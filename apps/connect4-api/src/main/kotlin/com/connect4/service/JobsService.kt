package com.connect4.service

import com.connect4.controller.JobsResultsNotifier
import com.connect4.model.Status
import com.connect4.model.DTO.BatchParameters
import com.connect4.model.Batch
import com.connect4.model.Jobs
import com.connect4.model.RemainingTasks
import com.connect4.repository.BatchesRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import java.util.LinkedList
import java.util.Queue
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicBoolean

@ApplicationScoped
class JobsService {
  @Inject
  private lateinit var dockerService: DockerService

  @Inject
  private lateinit var jobsResultsNotifier: JobsResultsNotifier

  @Inject
  private lateinit var batchesRepository: BatchesRepository

  private val batchQueue: Queue<Batch> = LinkedList()

  private val cores = Runtime.getRuntime().availableProcessors() - 3
  private val executor = Executors.newFixedThreadPool(cores) as ThreadPoolExecutor

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

        batch.status = Status.RUNNING

        batch.jobs.forEach {
          it.status = Status.PENDING
          batchesRepository.update(batch)
          jobsResultsNotifier.broadcast(batch.id, it.jobsId)
        }

        val futures = batch.jobs.map { job ->
          executor.submit {
            try {
              job.status = Status.RUNNING
              batchesRepository.update(batch)
              jobsResultsNotifier.broadcast(batch.id, job.jobsId)

              val stats = dockerService.getStats(batch.jobParameter)
              job.stats = stats

              batchesRepository.update(batch)

              job.status = Status.FINISHED
            } catch (e: Exception) {
              println(e.message)
              job.status = Status.FAILED
            }
            batchesRepository.update(batch)
            jobsResultsNotifier.broadcast(batch.id, job.jobsId)
          }
        }

        batch.status = Status.FINISHED
        batchesRepository.update(batch)

        futures.forEach { it.get() }
      }
    }

    batchWorker.isDaemon = true
    batchWorker.start()
  }

  fun addBatch(batchParameters: BatchParameters): String {
    val batchId = UUID.randomUUID().toString()

    val jobs = List(batchParameters.nbOfProcess) {
      val jobs = Jobs()
      jobs.batchId = batchId
      jobs.jobsId = UUID.randomUUID().toString()
      jobs
    }

    val batch = Batch()
    batch.id = batchId
    batch.jobs = jobs
    batch.jobParameter = batchParameters.jobParameter
    batchesRepository.persist(batch)

    jobs.forEach {
      jobsResultsNotifier.broadcast(batchId, it.jobsId)
    }

    batchQueue.add(batch)
    return batchId
  }

  fun getJob(batchId: String, jobId: String): Jobs? {
    val batch = batchesRepository.finByBatchId(batchId) ?: return null
    return batch.jobs.find { it.jobsId == jobId }
  }

  fun getAllJobs(): List<Jobs> {
    val batches = batchesRepository.listAll()
    return batches.flatMap { it.jobs }
  }

  fun getRemainingTasks(): RemainingTasks {
    val remainingJobs = if (batchQueue.isEmpty()) 0 else batchQueue.sumOf { it.jobs.size }

    val jobs = getAllJobs()

    val notStartedJobs = jobs.count { job -> job.status == Status.NOT_STARTED }
    val pendingJobs = jobs.count { job -> job.status == Status.PENDING }
    val runningJobs = jobs.count { job -> job.status == Status.RUNNING }
    val finishedJobs = jobs.count { job -> job.status == Status.FINISHED }
    val failedJobs = jobs.count { job -> job.status == Status.FAILED }

    return RemainingTasks(
      executor.activeCount,
      cores,
      batchQueue.size,
      remainingJobs,
      notStartedJobs,
      pendingJobs,
      runningJobs,
      finishedJobs,
      failedJobs
    )
  }
}
