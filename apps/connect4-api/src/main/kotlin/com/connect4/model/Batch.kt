package com.connect4.model

import io.quarkus.mongodb.panache.common.MongoEntity
import org.bson.codecs.pojo.annotations.BsonId

@MongoEntity(collection = "batches")
class Batch {
  @BsonId
  lateinit var id: String
  lateinit var jobs: List<Jobs>
  var status: Status = Status.NOT_STARTED
}
