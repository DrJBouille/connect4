package org.example

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.example.board.BoardImpl
import org.example.bot.BotImpl
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val redDeepness = args.getOrNull(0)?.toIntOrNull() ?: 1
    val yellowDeepness = args.getOrNull(1)?.toIntOrNull() ?: 1

    val stats = Stats(redDeepness = redDeepness, yellowDeepness = yellowDeepness)
    val board = BoardImpl()
    var isRedTurn = true
    var doesRedWin: Boolean? = null

    val gameTime = measureTimeMillis {
        val redBot = BotImpl(true, redDeepness)
        val yellowBot = BotImpl(false, yellowDeepness)

        while (true) {
            if (board.board.none {null in it}) break

            var coordinate: Coordinate
            val timeToMove = measureTimeMillis {
                coordinate = if (isRedTurn) redBot.getBestMove(board).first else yellowBot.getBestMove(board).first
            }

            val possibleMoves = board.getPossibleMoves()

            if (!possibleMoves.contains(coordinate)) continue

            board.addDiscs(coordinate, isRedTurn)

            stats.moves.add(Move(timeToMove, coordinate, board.board.toMutableList()))

            if (board.doesPlayerWin(coordinate, isRedTurn)) {
              doesRedWin = isRedTurn
              break
            }

            isRedTurn = !isRedTurn
        }
    }

    stats.gameTime = gameTime
    stats.doesRedWin = doesRedWin

    val mapper = jsonMapper().registerKotlinModule()
    println(mapper.writeValueAsString(stats))
    System.out.flush()
}
