package com.connect4.controller

import com.connect4.service.TasksService
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Response


@Path("/api/taks")
class TasksController {
  @Inject
  private lateinit var tasksService: TasksService

  @GET
  @Path("/{jobId}/{taskId}")
  fun getTask(@PathParam("jobId") jobId: String, @PathParam("taskId") taskId: String): Response {
    val task = tasksService.getTask(jobId, taskId) ?: return Response.status(Response.Status.NOT_FOUND).build()
    return Response.ok().entity(task).build()
  }

  @GET
  @Path("/{jobId}")
  fun getTasks(@PathParam("jobId") jobId: String): Response {
    return Response.ok().entity(tasksService.getTasks(jobId)).build()
  }
}
