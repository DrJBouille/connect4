package com.connect4.repository

import com.connect4.model.jobs.Jobs
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class JobsRepository : PanacheMongoRepository<Jobs> {
  fun findByJobId(id: String): Jobs? {
    return find("_id", id).firstResult()
  }

  fun findByJobParameters(redDeepness: Int, yellowDeepness: Int): List<Jobs> {
    return find(
      "jobParameter.redDeepness = ?1 and jobParameter.yellowDeepness = ?2",
      redDeepness,
      yellowDeepness
    ).list()
  }
}
