package org.example.board

interface Board {
    var board: MutableList<MutableList<Boolean?>>

    fun addDiscs(coordinate: Pair<Int, Int>, isRed: Boolean)
    fun getPossibleMoves(): MutableList<Pair<Int, Int>>
    fun doesPlayerWin(coordinate: Pair<Int, Int>, isRed: Boolean): Boolean
}