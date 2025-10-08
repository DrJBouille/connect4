package org.example.bot

import org.example.Coordinate
import org.example.board.Board

interface Bot {
    val isRed: Boolean
    val depth: Int

    fun getMinmax(board: Board, maximizingPlayer: Boolean = true, isActuallyRed: Boolean = isRed, actualDepth: Int = depth): Pair<Coordinate, Int>
}
