package com.connect4.model.jobs

import com.connect4.model.Status
import com.connect4.model.tasks.Tasks
import io.quarkus.mongodb.panache.common.MongoEntity
import org.bson.codecs.pojo.annotations.BsonId

@MongoEntity(collection = "jobs")
class Jobs {
  @BsonId
  lateinit var id: String
  lateinit var tasks: List<Tasks>
  var status: Status = Status.NOT_STARTED
}
