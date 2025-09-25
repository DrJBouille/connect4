package org.example.board

import org.example.Coordinate
import org.example.utils.checkDiscsOnARow

class BoardImpl : Board {
    override var board: MutableList<MutableList<Boolean?>> = mutableListOf(
        MutableList(7) {null},
        MutableList(7) {null},
        MutableList(7) {null},
        MutableList(7) {null},
        MutableList(7) {null},
        MutableList(7) {null}
    )

    override fun addDiscs(coordinate: Coordinate, isRed: Boolean) {
        board[coordinate.y][coordinate.x] = isRed
    }

    override fun getPossibleMoves(): MutableList<Coordinate> {
        val possibleMoves = mutableListOf<Coordinate>()

        for (col in 0 until board[0].size) {
            for (row in 0 until board.size) {
                if (board[row][col] == null) {
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
