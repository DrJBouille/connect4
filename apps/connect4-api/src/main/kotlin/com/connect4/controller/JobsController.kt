package com.connect4.controller

import com.connect4.service.JobsService
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Response


@Path("/api/jobs")
class JobsController {
  @Inject
  private lateinit var jobsService: JobsService

  @GET
  @Path("/{batchId}/{jobId}")
  fun getJob(@PathParam("batchId") batchId: String, @PathParam("jobId") jobId: String): Response {
    val job = jobsService.getJob(batchId, jobId) ?: return Response.status(Response.Status.NOT_FOUND).build()
    return Response.ok().entity(job).build()
  }

  @GET
  @Path("/{batchId}")
  fun getJobs(@PathParam("batchId") batchId: String): Response {
    return Response.ok().entity(jobsService.getJobs(batchId)).build()
  }
}
