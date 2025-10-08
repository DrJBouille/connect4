package org.example.bot

import org.example.Coordinate
import org.example.board.Board
import org.example.board.BoardImpl
import org.example.utils.checkDiscsOnARow

class BotImpl(override val isRed: Boolean, override val depth: Int) : Bot {
  override fun getMinmax(board: Board, maximizingPlayer: Boolean, isActuallyRed: Boolean, actualDepth: Int): Pair<Coordinate, Int> {
    val possibleMovesCoordinate = board.getPossibleMoves()

    if (actualDepth == 0 || isTerminal(board)) {
      return if (isTerminal(board)) {
        if (winningMove(board, isRed)) {
          Pair(Coordinate(0, 0), Int.MAX_VALUE)
          } else if (winningMove(board, !isRed)) {
          Pair(Coordinate(0, 0), Int.MIN_VALUE)
          } else {
          Pair(Coordinate(0, 0), 0)
          }
      } else {
        Pair(Coordinate(0, 0), scorePosition(board, isRed))
      }
    }

    if (maximizingPlayer) {
      var value = Int.MIN_VALUE
      var bestCoordinate = possibleMovesCoordinate.random()

      for (coordinate in possibleMovesCoordinate) {
        val fakeBoard = BoardImpl()
        fakeBoard.board = board.board.map { it.toMutableList() }.toMutableList()
        fakeBoard.addDiscs(coordinate, isActuallyRed)

        val newScore = getMinmax(fakeBoard, false, !isActuallyRed, actualDepth - 1).second

        if (newScore > value) {
          value = newScore
          bestCoordinate = coordinate
        }
      }

      return Pair(bestCoordinate, value)
    } else {
      var value = Int.MAX_VALUE
      var bestCoordinate = possibleMovesCoordinate.random()

      for (coordinate in possibleMovesCoordinate) {
        val fakeBoard = BoardImpl()
        fakeBoard.board = board.board.map { it.toMutableList() }.toMutableList()
        fakeBoard.addDiscs(coordinate, isActuallyRed)

        val newScore = getMinmax(fakeBoard, true, !isActuallyRed, actualDepth - 1).second

        if (newScore < value) {
          value = newScore
          bestCoordinate = coordinate
        }
      }

      return Pair(bestCoordinate, value)
    }
  }

  private fun isTerminal(board: Board): Boolean {
    return board.getPossibleMoves().isEmpty() || winningMove(board, true) || winningMove(board, false)
  }

  private fun scorePosition(board: Board, isRed: Boolean): Int {
    var score = 0
    val yCount = board.board.size
    val xCount = board.board[0].size
    val windowLength = 4
    val piece = if (isRed) 1 else 2

    val centreCol = xCount / 2
    val centreArray = board.board.map { it[centreCol] }
    score += (centreArray.groupingBy { it }.eachCount()[piece] ?: 0) * 3

    for (y in 0..<yCount) {
      val yArray = board.board[y]
      for (x in 0..< xCount - 3) {
        val window = yArray.slice(x until x + windowLength)
        score += evaluateWindow(window, isRed)
      }
    }

    for (x in 0..< xCount) {
      val xArray = board.board.map { it[x] }
      for (y in 0..< yCount - 3) {
        val window = xArray.slice(y until y + windowLength)
        score += evaluateWindow(window, isRed)
      }
    }

    for (x in 0..< xCount - 3) {
      for (y in 0..< yCount - 3) {
        val window = List(windowLength) { i -> board.board[y + i][x + i] }
        score += evaluateWindow(window, isRed)
      }
    }

    for (x in 0..< xCount - 3) {
      for (y in 0..< yCount - 3) {
        val window = List(windowLength) { i -> board.board[y + 3 - i][x + i] }
        score += evaluateWindow(window, isRed)
      }
    }

    return score
  }

  private fun evaluateWindow(window: List<Int>, isRed: Boolean): Int {
    var score = 0
    val piece = if (isRed) 1 else 2
    val oppPiece = if (isRed) 2 else 1

    val frequencies = window.groupingBy { it }.eachCount()

    if (frequencies[piece] == 4) {
      score += 100
    } else if (frequencies[piece] == 3 && frequencies[0] == 1) {
      score += 5
    } else if (frequencies[piece] == 2 && frequencies[0] == 2) {
      score += 2
    }

    if (frequencies[oppPiece] == 4) {
      score -= 100
    } else if (frequencies[oppPiece] == 3 && frequencies[0] == 1) {
      score -= 5
    } else if (frequencies[oppPiece] == 2 && frequencies[0] == 2) {
      score -= 2
    }

    return score
  }

  private fun winningMove(board: Board, isRed: Boolean): Boolean {
    val piece = if (isRed) 1 else 2

    for ((y, row) in board.board.withIndex()) {
      for ((x, value) in row.withIndex()) {
        if (value != piece) continue
        if (checkDiscsOnARow(board.board, Coordinate(x, y), isRed) >= 4) return true
      }
    }

    return false
  }
}
