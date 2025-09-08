package org.example.board

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

    override fun addDiscs(coordinate: Pair<Int, Int>, isRed: Boolean) {
        board[coordinate.first][coordinate.second] = isRed
    }

    override fun getPossibleMoves(): MutableList<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()

        for (col in 0 until board[0].size) {
            for (row in 0 until board.size) {
                if (board[row][col] == null) {
                    possibleMoves.add(Pair(row, col))
                    break
                }
            }
        }
        return possibleMoves
    }

    override fun doesPlayerWin(coordinate: Pair<Int, Int>, isRed: Boolean): Boolean {
        return checkDiscsOnARow(board, coordinate, isRed) >= 4
    }
}