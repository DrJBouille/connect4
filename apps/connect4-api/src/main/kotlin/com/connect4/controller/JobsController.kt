package com.connect4.controller

import com.connect4.model.DTO.BatchIdDTO
import com.connect4.model.DTO.BatchParameters
import com.connect4.service.JobsService
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response


@Path("/api/jobs")
class JobsController {
  @Inject
  private lateinit var jobsService: JobsService

  @POST
  @Path("/start")
  fun addProcess(batchParameters: BatchParameters): Response {
    if (batchParameters.nbOfProcess == 0) return Response.status(Response.Status.BAD_REQUEST).entity("nbOfProcess should be higher than 0").build()

    val batchId = jobsService.addBatch(batchParameters)
    jobsService.startProcess()

    return Response.ok().entity(BatchIdDTO(batchId)).build()
  }

  @GET
  @Path("/{id}")
  fun getJob(@PathParam("id") id: String): Response {
    val job = jobsService.getJob(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
    return Response.ok().entity(job).build()
  }
}
