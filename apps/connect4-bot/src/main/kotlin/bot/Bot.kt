package org.example.bot

import org.example.Coordinate
import org.example.board.Board

interface Bot {
    val isRed: Boolean
    val deepness: Int

    fun getBestMove(board: Board, actualDeepness: Int = 1, isActuallyRedTurn: Boolean = this.isRed): Pair<Coordinate, Int>
}
