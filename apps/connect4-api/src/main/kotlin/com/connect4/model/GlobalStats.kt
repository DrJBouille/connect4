package com.connect4.model

data class GlobalStats(
  val averageGameTime: Double,
  val minGameTime: Long,
  val maxGameTime: Long,
  val averageMoves: Double,
  val minMoves: Int,
  val maxMoves: Int,
  val redWins: Int,
  val yellowWins: Int,
  val draws: Int,
)
