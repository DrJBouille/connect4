package com.connect4.repository

import com.connect4.model.Batch
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class BatchesRepository : PanacheMongoRepository<Batch> {
  fun finByBatchId(id: String): Batch? {
    return find("_id", id).firstResult()
  }
}
