package com.connect4.service

import com.connect4.model.tasks.Tasks
import com.connect4.repository.JobsRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class TasksService {

  @Inject private lateinit var jobsRepository: JobsRepository

  fun getTasks(): List<Tasks> {
    val batches = jobsRepository.listAll()
    return batches.flatMap { it.tasks }
  }

  fun getTasks(batchId: String): List<Tasks> {
    val batch = jobsRepository.findByJobId(batchId) ?: return emptyList()
    return batch.tasks
  }

  fun getTask(batchId: String, jobId: String): Tasks? {
    val batch = jobsRepository.findByJobId(batchId) ?: return null
    return batch.tasks.find { it.id == jobId }
  }
}
