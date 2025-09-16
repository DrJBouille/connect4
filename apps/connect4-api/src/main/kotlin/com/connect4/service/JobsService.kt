package com.connect4.service

import com.connect4.model.BotsParameters
import com.connect4.model.Stats
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.Executors

@ApplicationScoped
class JobsService {
  @Inject
  private lateinit var dockerService: DockerService

  private val jobsQueue: Queue<BotsParameters> = LinkedList()
  private val jobsResults = mutableMapOf<String, Stats>()
  private val cores = Runtime.getRuntime().availableProcessors()

  fun startProcess() {
    val executor = Executors.newFixedThreadPool(cores)

    val worker = Thread {
      while(true) {
        val botsParameters = jobsQueue.poll()
        executor.submit {
          val stats = dockerService.getStats(botsParameters)
        }
      }
    }

    worker.isDaemon = true
    worker.start()
  }

  fun addProcess(botsParameters: BotsParameters) {
    jobsQueue.add(botsParameters)
  }

  fun handleProcess(botsParameters: BotsParameters) {
    val stats = dockerService.getStats(botsParameters)
  }
}
