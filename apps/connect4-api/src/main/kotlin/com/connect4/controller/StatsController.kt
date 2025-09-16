package com.connect4.controller

import com.connect4.model.DTO.ProcessParameters
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response


@Path("/api/stats")
class StatsController {

  @POST
  @Path("/start")
  fun addProcess(processParameters: ProcessParameters): Response {
    if (processParameters.nbOfProcess == 0) return Response.status(Response.Status.BAD_REQUEST).entity("nbOfProcess should be higher than 0").build()

    repeat(processParameters.nbOfProcess) {
      processQueue.add(processParameters.botsParameters)
    }

    return Response.ok().entity("${processParameters.nbOfProcess} process added to queue").build()
  }
}
