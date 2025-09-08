package org.example

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.example.board.BoardImpl
import org.example.bot.BotImpl
import kotlin.system.measureTimeMillis

fun main() {
    val stats = Stats()
    val board = BoardImpl()
    var hasWin = false
    var isRedTurn = true

    val gameTime = measureTimeMillis {
        val redBot = BotImpl(true, 1)
        val yellowBot = BotImpl(false, 7)

        while (true) {
            if (board.board.none {null in it}) return

            var coordinate: Pair<Int, Int>
            val timeToMove = measureTimeMillis {
                coordinate = if (isRedTurn) redBot.getBestMove(board).first else yellowBot.getBestMove(board).first
            }

            val possibleMoves = board.getPossibleMoves()

            if (!possibleMoves.contains(coordinate)) continue

            board.addDiscs(coordinate, isRedTurn)

            stats.moves.add(Move(timeToMove, coordinate, board.board))

            if (board.doesPlayerWin(coordinate, isRedTurn)) break

            isRedTurn = !isRedTurn
        }
    }

    stats.gameTime = gameTime
    stats.doesRedWin = isRedTurn

    val mapper = jsonMapper().registerKotlinModule()
    println(mapper.writeValueAsString(stats))
}