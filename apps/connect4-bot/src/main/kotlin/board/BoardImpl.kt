package org.example.board

import org.example.Coordinate
import org.example.utils.checkDiscsOnARow

class BoardImpl : Board {
    override var board: MutableList<MutableList<Int>> = mutableListOf(
        MutableList(7) {0},
        MutableList(7) {0},
        MutableList(7) {0},
        MutableList(7) {0},
        MutableList(7) {0},
        MutableList(7) {0}
    )

    override fun addDiscs(coordinate: Coordinate, isRed: Boolean) {
        val piece = if (isRed) 1 else 2
        board[coordinate.y][coordinate.x] = piece
    }

    override fun getPossibleMoves(): MutableList<Coordinate> {
        val possibleMoves = mutableListOf<Coordinate>()

        for (col in 0 until board[0].size) {
            for (row in 0 until board.size) {
                if (board[row][col] == 0) {
                    possibleMoves.add(Coordinate(col, row))
                    break
                }
            }
        }
        return possibleMoves
    }

    override fun doesPlayerWin(coordinate: Coordinate, isRed: Boolean): Boolean {
        return checkDiscsOnARow(board, coordinate, isRed) >= 4
    }
}
