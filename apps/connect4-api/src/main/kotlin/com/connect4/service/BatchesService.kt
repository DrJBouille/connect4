package com.connect4.service

import com.connect4.controller.BatchesNotifier
import com.connect4.controller.JobsNotifier
import com.connect4.model.Batch
import com.connect4.model.DTO.BatchParameters
import com.connect4.model.GlobalStats
import com.connect4.model.Jobs
import com.connect4.model.RemainingTasks
import com.connect4.model.Status
import com.connect4.repository.BatchesRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.LinkedList
import java.util.Queue
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicBoolean

@ApplicationScoped
class BatchesService {

  @Inject private lateinit var batchesRepository: BatchesRepository
  @Inject private lateinit var jobsNotifier: JobsNotifier
  @Inject private lateinit var batchesNotifier: BatchesNotifier
  @Inject private lateinit var jobsService: JobsService
  @Inject private lateinit var dockerService: DockerService

  val batchQueue: Queue<Batch> = LinkedList()

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
        batchesRepository.update(batch)
        batchesNotifier.broadcast(batch.id)

        batch.jobs.forEach {
          it.status = Status.PENDING
          batchesRepository.update(batch)
          jobsNotifier.broadcast(batch.id, it.jobsId)
        }

        val futures = batch.jobs.map { job ->
          executor.submit {
            try {
              job.status = Status.RUNNING
              batchesRepository.update(batch)
              jobsNotifier.broadcast(batch.id, job.jobsId)

              val stats = dockerService.getStats(batch.jobParameter)
              job.stats = stats

              batchesRepository.update(batch)

              job.status = Status.FINISHED
            } catch (e: Exception) {
              println(e.message)
              job.status = Status.FAILED
            }
            batchesRepository.update(batch)
            jobsNotifier.broadcast(batch.id, job.jobsId)
          }
        }

        batch.status = Status.FINISHED
        batchesRepository.update(batch)
        batchesNotifier.broadcast(batch.id)

        futures.forEach { it.get() }
      }
    }

    batchWorker.isDaemon = true
    batchWorker.start()
  }

  fun getBatches(): List<Batch> {
    return batchesRepository.listAll()
  }

  fun getBatch(batchId: String): Batch? {
    return batchesRepository.finByBatchId(batchId)
  }

  fun createBatch(batchParameters: BatchParameters): String {
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
      jobsNotifier.broadcast(batchId, it.jobsId)
    }

    batchesNotifier.broadcast(batchId)

    batchQueue.add(batch)
    return batchId
  }

  fun getRemainingTasks(): RemainingTasks {
    val batches = getBatches()

    val notStartedJobs = batches.count { batch -> batch.status == Status.NOT_STARTED }
    val runningJobs = batches.count { batch -> batch.status == Status.RUNNING }
    val finishedJobs = batches.count { batch -> batch.status == Status.FINISHED }

    return RemainingTasks(
      executor.activeCount,
      cores,
      batchQueue.size,
      notStartedJobs,
      runningJobs,
      finishedJobs
    )
  }

  fun getGlobalStats(redDeepness: Int, yellowDeepness: Int): GlobalStats? {
    val batches = batchesRepository.findByBatchJobParameter(redDeepness, yellowDeepness)

    if (batches.isEmpty()) return null

    val gamesTime = mutableListOf<Long>()
    val moves = mutableListOf<Int>()
    var redWins = 0
    var yellowWins = 0
    var draws = 0

    batches.forEach {
      batch -> batch.jobs.forEach {
        job -> if (job.status == Status.FINISHED) {
          gamesTime.add(job.stats!!.gameTime)
          moves.add(job.stats!!.moves.size)

          if (job.stats!!.doesRedWin == null) draws++
          else if (job.stats!!.doesRedWin!!) redWins++
          else yellowWins++
        }
      }
    }

    return GlobalStats(
      gamesTime.average(),
      gamesTime.min(),
      gamesTime.max(),
      moves.average(),
      moves.min(),
      moves.max(),
      redWins,
      yellowWins,
      draws
    )
  }
}
