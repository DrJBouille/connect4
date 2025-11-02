package com.connect4.controller

import com.connect4.model.IdDTO
import com.connect4.model.jobs.JobsParameters
import com.connect4.service.JobsService
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Response

@Path("/api/jobs")
class JobController {

  @Inject private lateinit var jobsService: JobsService

  @POST
  @Path("/start")
  fun createBatches(jobsParameters: JobsParameters): Response {
    for (parameters in jobsParameters.parameters) {
      if (parameters.nbOfProcess <= 0) return Response.status(Response.Status.BAD_REQUEST).entity("The number of process should be higher than 0").build()
      if (parameters.redDeepness <= 0) return Response.status(Response.Status.BAD_REQUEST).entity("red depth should be higher than 0").build()
      if (parameters.yellowDeepness <= 0) return Response.status(Response.Status.BAD_REQUEST).entity("yellow depth should be higher than 0").build()
    }

    val jobId = jobsService.createJob(jobsParameters)
    jobsService.startProcess()

    return Response.ok().entity(IdDTO(jobId)).build()
  }

  @GET
  fun getJobs(): Response {
    return Response.ok().entity(jobsService.getJobs()).build()
  }

  @GET
  @Path("/{jobId}")
  fun getJob(@PathParam("jobId") jobId: String): Response {
    val job = jobsService.getJob(jobId) ?: return Response.status(Response.Status.NOT_FOUND).build()
    return Response.ok().entity(job).build()
  }

  @GET
  @Path("/remainingJobs")
  fun getRemainingTasks(): Response {
    return Response.ok().entity(jobsService.getRemainingTasks()).build()
  }
}
