package com.connect4.controller

import com.connect4.service.BatchesService
import com.connect4.service.StatsService
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Response

@Path("/api/stats")
class StatsController {

  @Inject private lateinit var statsService: StatsService

  @GET
  @Path("/stats/{redDeepness}/{yellowDeepness}")
  fun getStatsByDeepness(@PathParam("redDeepness") redDeepness: Int, @PathParam("yellowDeepness") yellowDeepness: Int): Response {
    val globalStats = statsService.getGlobalStats(redDeepness, yellowDeepness) ?: return Response.status(Response.Status.NO_CONTENT).build()
    return Response.ok().entity(globalStats).build()
  }
}
