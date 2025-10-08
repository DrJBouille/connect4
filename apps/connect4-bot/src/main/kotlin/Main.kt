package org.example

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.example.board.BoardImpl
import org.example.bot.BotImpl
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val redDeepness = args.getOrNull(0)?.toIntOrNull() ?: 3
    val yellowDeepness = args.getOrNull(1)?.toIntOrNull() ?: 3

    val stats = Stats(redDeepness = redDeepness, yellowDeepness = yellowDeepness)
    val board = BoardImpl()
    var isRedTurn = true
    var doesRedWin: Boolean? = null

    val gameTime = measureTimeMillis {
        val redBot = BotImpl(true, redDeepness)
        val yellowBot = BotImpl(false, yellowDeepness)

        while (true) {
            if (board.board.none {0 in it}) break

            var coordinate: Coordinate
            val timeToMove = measureTimeMillis {
                val value = if (isRedTurn) redBot.getMinmax(board) else yellowBot.getMinmax(board)
                coordinate = value.first
            }

            val possibleMoves = board.getPossibleMoves()

            if (!possibleMoves.contains(coordinate)) break


            board.addDiscs(coordinate, isRedTurn)

            stats.moves.add(Move(timeToMove, isRedTurn, coordinate))

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
