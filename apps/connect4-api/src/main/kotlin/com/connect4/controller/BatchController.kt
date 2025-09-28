package com.connect4.controller

import com.connect4.model.DTO.BatchParameters
import com.connect4.model.DTO.BatchIdDTO
import com.connect4.service.BatchesService
import com.connect4.service.JobsService
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Response

@Path("/api/batches")
class BatchController {

  @Inject private lateinit var batchesService: BatchesService

  @POST
  @Path("/start")
  fun createBatches(batchParameters: BatchParameters): Response {
    if (batchParameters.nbOfProcess == 0) return Response.status(Response.Status.BAD_REQUEST).entity("nbOfProcess should be higher than 0").build()

    val batchId = batchesService.createBatch(batchParameters)
    batchesService.startProcess()

    return Response.ok().entity(BatchIdDTO(batchId)).build()
  }

  @GET
  fun getBatches(): Response {
    return Response.ok().entity(batchesService.getBatches()).build()
  }

  @GET
  @Path("/{batchId}")
  fun getBatch(@PathParam("batchId") batchId: String): Response {
    val batch = batchesService.getBatch(batchId) ?: return Response.status(Response.Status.NOT_FOUND).build()
    return Response.ok().entity(batch).build()
  }

  @GET
  @Path("/remainingTasks")
  fun getRemainingTasks(): Response {
    return Response.ok().entity(batchesService.getRemainingTasks()).build()
  }
}
