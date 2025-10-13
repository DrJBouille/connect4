package com.connect4.model.entity

import com.connect4.model.entity.Jobs
import com.connect4.model.Status
import io.quarkus.mongodb.panache.common.MongoEntity
import org.bson.codecs.pojo.annotations.BsonId

@MongoEntity(collection = "batches")
class Batch {
  @BsonId
  lateinit var id: String
  lateinit var jobs: List<Jobs>
  var status: Status = Status.NOT_STARTED
}
