package com.connect4.service

import com.connect4.model.stats.GlobalStats
import com.connect4.model.Status
import com.connect4.repository.JobsRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class StatsService {

  @Inject private lateinit var jobsRepository: JobsRepository

  fun getGlobalStats(redDeepness: Int, yellowDeepness: Int): GlobalStats? {
    val batches = jobsRepository.findByJobParameters(redDeepness, yellowDeepness)

    if (batches.isEmpty()) return null

    val gamesTime = mutableListOf<Long>()
    val moves = mutableListOf<Int>()
    var redWins = 0
    var yellowWins = 0
    var draws = 0
    val movesTime = mutableListOf<MutableList<Long>>()

    for (batch in batches) {
      val maxNbOfMoves = batch.tasks.maxOfOrNull { it.stats?.moves?.size ?: 0 } ?: 0

      for (job in batch.tasks) {
        if (job.status != Status.FINISHED) continue

        gamesTime.add(job.stats!!.gameTime)
        moves.add(job.stats!!.moves.size)

        var totalTime = 0L

        for ((index, move) in job.stats!!.moves.withIndex()) {
          totalTime += move.time
          if (movesTime.getOrNull(index) == null) movesTime.add(index, mutableListOf(move.time))
          else movesTime[index].add(move.time)
        }

        for (i in job.stats!!.moves.size until maxNbOfMoves) {
          if (movesTime.getOrNull(i) == null) movesTime[i] = mutableListOf(totalTime)
          else movesTime[i].add(totalTime)
        }

        if (job.stats!!.doesRedWin == null) draws++
        else if (job.stats!!.doesRedWin!!) redWins++
        else yellowWins++
      }
    }

    val movesTimeMap = movesTime.mapIndexed { index, moveTimes -> index to moveTimes.average() }.toMap()

    return GlobalStats(
      gamesTime.average(),
      gamesTime.min(),
      gamesTime.max(),
      moves.average(),
      moves.min(),
      moves.max(),
      movesTimeMap,
      redWins,
      yellowWins,
      draws
    )
  }
}
