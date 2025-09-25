package org.example.utils

import org.example.Coordinate

fun checkDiscsOnARow(
    board: MutableList<MutableList<Boolean?>>,
    coordinate: Coordinate,
    isRed: Boolean
): Int {
    val row = coordinate.y
    val col = coordinate.x

    val directions = listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)

    var count = 1

    for((directionR, directionC) in directions) {
        val total = 1 + countInDirection(board, row, col, directionR, directionC, isRed) + countInDirection(board, row, col, -directionR, -directionC, isRed)
        count = maxOf(count, total)
    }

    return count
}

fun countInDirection(
    board: List<List<Boolean?>>,
    row: Int, col: Int,
    dr: Int, dc: Int,
    isRed: Boolean
): Int {
    var r = row + dr
    var c = col + dc
    var count = 0

    while (r in board.indices && c in board[r].indices && board[r][c] == isRed) {
        count++
        r += dr
        c += dc
    }

    return count
}
