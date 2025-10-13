package com.connect4.repository

import com.connect4.model.entity.Batch
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class BatchesRepository : PanacheMongoRepository<Batch> {
  fun finByBatchId(id: String): Batch? {
    return find("_id", id).firstResult()
  }

  fun findByBatchJobParameter(redDeepness: Int, yellowDeepness: Int): List<Batch> {
    return find(
      "jobParameter.redDeepness = ?1 and jobParameter.yellowDeepness = ?2",
      redDeepness,
      yellowDeepness
    ).list()
  }
}
