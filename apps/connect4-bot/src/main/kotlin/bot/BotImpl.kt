package org.example.bot

import org.example.Coordinate
import org.example.board.Board
import org.example.board.BoardImpl
import org.example.utils.checkDiscsOnARow

class BotImpl(override val isRed: Boolean, override val deepness: Int) : Bot {
  override fun getBestMove(
    board: Board,
    actualDeepness: Int,
    isActuallyRedTurn: Boolean
  ): Pair<Coordinate, Int> {
    val possibleMovesCoordinate = board.getPossibleMoves()
    var bestMoveCoordinate: Coordinate? = null
    val isMyTurn = isActuallyRedTurn == isRed
    var bestScore = if (isMyTurn) Int.MIN_VALUE else Int.MAX_VALUE

    if (possibleMovesCoordinate.isEmpty()) return Coordinate(-1, -1) to 0

    for (coordinate in possibleMovesCoordinate) {
      val fakeBoard = BoardImpl()
      fakeBoard.board = board.board.map { it.toMutableList() }.toMutableList()
      fakeBoard.addDiscs(coordinate, isActuallyRedTurn)

      if (fakeBoard.doesPlayerWin(coordinate, isActuallyRedTurn)) return Pair(coordinate, if (isActuallyRedTurn == isRed) Int.MAX_VALUE else Int.MIN_VALUE)

      val score = if (fakeBoard.doesPlayerWin(coordinate, isActuallyRedTurn)) {
        if (isActuallyRedTurn == isRed) 100000 - actualDeepness else -100000 + actualDeepness
      } else if (actualDeepness < deepness) {
        getBestMove(fakeBoard, actualDeepness + 1, !isActuallyRedTurn).second
      } else {
        evaluateBoard(fakeBoard, coordinate, isActuallyRedTurn)
      }

      if (isMyTurn) {
        if (score > bestScore) {
          bestScore = score
          bestMoveCoordinate = coordinate
        }
      } else {
        if (score < bestScore) {
          bestScore = score
          bestMoveCoordinate = coordinate
        }
      }
    }

    return Pair(bestMoveCoordinate ?: possibleMovesCoordinate.random(), bestScore)
  }

  private fun evaluateBoard(board: Board, coordinate: Coordinate, isActuallyRedTurn: Boolean): Int {
    var score = 0

    val streak = checkDiscsOnARow(board.board, coordinate, isActuallyRedTurn)
    val baseScore = when (streak) {
      4 -> 10000
      3 -> 100
      2 -> 10
      1 -> 1
      else -> 0
    }

    score += if (isActuallyRedTurn == isRed) baseScore else -baseScore

    val possibleMoves = board.getPossibleMoves()
    for (move in possibleMoves) {
      val testBoard = BoardImpl()
      testBoard.board = board.board.map { it.toMutableList() }.toMutableList()
      testBoard.addDiscs(move, !isActuallyRedTurn)

      if (testBoard.doesPlayerWin(move, !isActuallyRedTurn)) {
        score += if (isRed) -5000 else 5000
        break
      }

      val opponentStreak = checkDiscsOnARow(testBoard.board, move, !isActuallyRedTurn)
      if (opponentStreak == 3) {
        score += if (isRed) -50 else 50
      }
    }

    return score
  }
}
