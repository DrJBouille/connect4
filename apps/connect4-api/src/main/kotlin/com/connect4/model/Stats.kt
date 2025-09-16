package com.connect4.model

data class Stats(
  var gameTime: Long = 0,
  var doesRedWin: Boolean = true,
  val moves: MutableList<Move> = mutableListOf()
)

data class Move(
  val time: Long = 0,
  val coordinate: Pair<Int, Int> = Pair(0, 0),
  val board: MutableList<MutableList<Boolean?>> = mutableListOf()
)

