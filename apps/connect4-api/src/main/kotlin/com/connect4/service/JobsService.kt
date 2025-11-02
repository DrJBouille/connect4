package com.connect4.service

import com.connect4.controller.JobsNotifier
import com.connect4.controller.TasksNotifier
import com.connect4.model.jobs.Jobs
import com.connect4.model.jobs.JobsParameters
import com.connect4.model.stats.RemainingTasks
import com.connect4.model.Status
import com.connect4.model.tasks.Tasks
import com.connect4.repository.JobsRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.LinkedList
import java.util.Queue
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicBoolean

@ApplicationScoped
class JobsService {

  @Inject private lateinit var jobsRepository: JobsRepository
  @Inject private lateinit var tasksNotifier: TasksNotifier
  @Inject private lateinit var jobsNotifier: JobsNotifier
  @Inject private lateinit var dockerService: DockerService

  val jobsQueue: Queue<Jobs> = LinkedList()

  private val cores = Runtime.getRuntime().availableProcessors() - 3
  private val executor = Executors.newFixedThreadPool(cores) as ThreadPoolExecutor
  private val isRunning = AtomicBoolean(false)

  fun startProcess() {
    if (isRunning.get()) return

    isRunning.set(true)
    val batchWorker = Thread {
      while(true) {
        val job = jobsQueue.poll()

        if (job == null) {
          isRunning.set(false)
          break
        }

        job.status = Status.RUNNING
        jobsRepository.update(job)
        jobsNotifier.broadcast(job.id)

        job.tasks.forEach {
          it.status = Status.PENDING
          jobsRepository.update(job)
          tasksNotifier.broadcast(job.id, it.id)
        }

        val futures = job.tasks.map { task ->
          executor.submit {
            try {
              task.status = Status.RUNNING
              jobsRepository.update(job)
              tasksNotifier.broadcast(task.jobId, task.id)

              val stats = dockerService.getStats(task)
              task.stats = stats

              jobsRepository.update(job)

              task.status = Status.FINISHED
            } catch (e: Exception) {
              println(e.message)
              task.status = Status.FAILED
            }
            jobsRepository.update(job)
            tasksNotifier.broadcast(task.jobId, task.id)
          }
        }

        job.status = Status.FINISHED
        jobsRepository.update(job)
        jobsNotifier.broadcast(job.id)

        futures.forEach { it.get() }
      }
    }

    batchWorker.isDaemon = true
    batchWorker.start()
  }

  fun getJobs(): List<Jobs> {
    return jobsRepository.listAll()
  }

  fun getJob(batchId: String): Jobs? {
    return jobsRepository.findByJobId(batchId)
  }

  fun createJob(jobsParameters: JobsParameters): String {
    val jobId = UUID.randomUUID().toString()

    val tasks = mutableListOf<Tasks>()

    for (parameters in jobsParameters.parameters) {
      tasks.addAll(List(parameters.nbOfProcess) {
        val tasks = Tasks()
        tasks.jobId = jobId
        tasks.id = UUID.randomUUID().toString()
        tasks.parameters = parameters
        tasks
      })
    }

    val batch = Jobs()
    batch.id = jobId
    batch.tasks = tasks
    jobsRepository.persist(batch)

    tasks.forEach {
      tasksNotifier.broadcast(jobId, it.id)
    }

    jobsNotifier.broadcast(jobId)

    jobsQueue.add(batch)
    return jobId
  }

  fun getRemainingTasks(): RemainingTasks {
    val batches = getJobs()

    val notStartedJobs = batches.count { batch -> batch.status == Status.NOT_STARTED }
    val runningJobs = batches.count { batch -> batch.status == Status.RUNNING }
    val finishedJobs = batches.count { batch -> batch.status == Status.FINISHED }

    return RemainingTasks(
      executor.activeCount,
      cores,
      jobsQueue.size,
      notStartedJobs,
      runningJobs,
      finishedJobs
    )
  }
}
