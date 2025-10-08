package org.example

data class Stats(
    var gameTime: Long = 0,
    var doesRedWin: Boolean? = null,
    var redDeepness: Int = 1,
    var yellowDeepness: Int = 1,
    var moves: MutableList<Move> = mutableListOf()
)

data class Move(
    var time: Long = 0,
    var isRedTurn: Boolean = true,
    var coordinate: Coordinate = Coordinate(),
)

data class Coordinate(
  var x: Int = 0,
  var y: Int = 0
)
