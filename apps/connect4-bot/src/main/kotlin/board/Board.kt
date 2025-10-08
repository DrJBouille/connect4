package org.example.board

import org.example.Coordinate

interface Board {
    var board: MutableList<MutableList<Int>>

    fun addDiscs(coordinate: Coordinate, isRed: Boolean)
    fun getPossibleMoves(): MutableList<Coordinate>
    fun doesPlayerWin(coordinate: Coordinate, isRed: Boolean): Boolean
}
