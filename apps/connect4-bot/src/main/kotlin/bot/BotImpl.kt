package org.example.bot

import org.example.Coordinate
import org.example.board.Board
import org.example.board.BoardImpl
import org.example.utils.checkDiscsOnARow

class BotImpl(override val isRed: Boolean, override val deepness: Int) : Bot {
    override fun getBestMove(
        board: Board,
        isMyTurn: Boolean,
        actualDeepness: Int
    ): Pair<Coordinate, Int> {
        val possibleMoves = board.getPossibleMoves()

        val isActuallyRed = if (isMyTurn) isRed else !isRed

        if (actualDeepness == deepness || possibleMoves.isEmpty()) {
            var bestMove: Coordinate? = null
            var bestScore = if (isMyTurn) Int.MIN_VALUE else Int.MAX_VALUE

            for (move in possibleMoves) {
                val fakeBoard = BoardImpl()
                fakeBoard.board = board.board.map { it.toMutableList() }.toMutableList()
                fakeBoard.addDiscs(move, isActuallyRed)

                val score = evaluateBoard(fakeBoard, isRed)

                if (isMyTurn && score > bestScore) {
                    bestScore = score
                    bestMove = move
                } else if (!isMyTurn && score < bestScore) {
                    bestScore = score
                    bestMove = move
                }
            }

            return if (possibleMoves.isEmpty()) Pair(Coordinate(-1, -1), bestScore)
            else Pair(bestMove ?: possibleMoves.random(), bestScore)
        }

        val bestMoves = mutableListOf<Coordinate>()
        var bestValue = if (isMyTurn) Int.MIN_VALUE else Int.MAX_VALUE

        for (move in possibleMoves) {
            val fakeBoard = BoardImpl()
            fakeBoard.board = board.board.map { it.toMutableList() }.toMutableList()
            fakeBoard.addDiscs(move, isActuallyRed)

            val (_, childValue) = getBestMove(fakeBoard, !isMyTurn, actualDeepness + 1)

            if (isMyTurn) {
                if (childValue > bestValue) {
                    bestValue = childValue
                    bestMoves.clear()
                    bestMoves.add(move)
                } else if (childValue == bestValue) {
                    bestMoves.add(move)
                }
            } else {
                if (childValue < bestValue) {
                    bestValue = childValue
                    bestMoves.clear()
                    bestMoves.add(move)
                } else if (childValue == bestValue) {
                    bestMoves.add(move)
                }
            }
        }

        val chosenMove = if (bestMoves.isEmpty()) possibleMoves.random() else bestMoves.random()
        return Pair(chosenMove, bestValue)
    }

    private fun evaluateBoard(board: Board, isRed: Boolean): Int {
        var score = 0
        val possibleMoves = board.getPossibleMoves()
        for (move in possibleMoves) {
            val playerNb = checkDiscsOnARow(board.board, move, isRed)
            score += when (playerNb) {
                4 -> 10000
                3 -> 100
                2 -> 10
                1 -> 1
                else -> 0
            }

            val opponentNb = checkDiscsOnARow(board.board, move, !isRed)
            score -= when (opponentNb) {
                4 -> 10000
                3 -> 100
                2 -> 10
                1 -> 1
                else -> 0
            }
        }

        return score
    }
}
