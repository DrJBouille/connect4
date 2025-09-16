package com.connect4.service

import com.connect4.model.BotsParameters
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.Executors

@ApplicationScoped
class ProcessService {
  @Inject
  private lateinit var statsService: StatsService

  private val processQueue: Queue<BotsParameters> = LinkedList()
  private val cores = Runtime.getRuntime().availableProcessors()

  fun startProcess() {
    val executor = Executors.newFixedThreadPool(cores)

    val worker = Thread {
      while(true) {
        val botsParameters = processQueue.poll()
        executor.submit {
          val stats = statsService.getStats(botsParameters)
        }
      }
    }

    worker.isDaemon = true
    worker.start()
  }

  fun addProcess(botsParameters: BotsParameters) {
    processQueue.add(botsParameters)
  }

  fun handleProcess(botsParameters: BotsParameters) {
    val stats = statsService.getStats(botsParameters)
  }
}
