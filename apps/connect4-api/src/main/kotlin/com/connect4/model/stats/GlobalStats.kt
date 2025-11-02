package com.connect4.model.stats

data class GlobalStats(
  val averageGameTime: Double,
  val minGameTime: Long,
  val maxGameTime: Long,
  val averageMoves: Double,
  val minMoves: Int,
  val maxMoves: Int,
  val movesTimeMap: Map<Int, Double>,
  val redWins: Int,
  val yellowWins: Int,
  val draws: Int,
)
