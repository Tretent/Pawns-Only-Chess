package chess

const val CHESS_COLUMN_NUMBER = 8

enum class SquareType(val representation: String) {
    WHITE(" W "), BLACK(" B "), EMPTY("   ")
}

enum class GameState {
    PLAY, END
}

class Chessboard {
    private var gameState = GameState.PLAY

    fun readPlayerNames(): List<String> {
        println("First Player's name:")
        val player1name = readln().trim()
        println("Second Player's name:")
        val player2name = readln().trim()
        return listOf(player1name, player2name)
    }

    fun printChessboard() {
        val verticalSide = "|"
        val horizontalSide = "+---"
        val indentation = "  "
        val whitePawnsRank = 2
        val blackPawnsRank = 7

        fun drawHorizontalLine() {
            println(indentation + horizontalSide.repeat(CHESS_COLUMN_NUMBER) + "+")
        }

        fun drawVerticalLine(rankLabel: Int, squareType: SquareType) {
            println("$rankLabel " + (verticalSide + squareType.representation).repeat(CHESS_COLUMN_NUMBER) + verticalSide)
        }

        fun drawFilesLabel() {
            print(indentation)
            ('a'..'h').forEach {
                print("  $it ")
            }
        }

        (CHESS_COLUMN_NUMBER downTo 1).forEach {
            drawHorizontalLine()
            when (it) {
                whitePawnsRank -> drawVerticalLine(it, SquareType.WHITE)
                blackPawnsRank -> drawVerticalLine(it, SquareType.BLACK)
                else -> drawVerticalLine(it, SquareType.EMPTY)
            }
        }
        drawHorizontalLine()
        drawFilesLabel()
        println()
    }

    fun play(player1name: String, player2name: String) {
        while (true) {
            if (gameState == GameState.PLAY) playerTurn(player1name) else break
            if (gameState == GameState.PLAY) playerTurn(player2name) else break
        }
    }

    private fun playerTurn(playerName: String) {
        while (true) {
            println("$playerName's turn:")
            when (val command = readln().trim()) {
                "exit" -> {
                    println("Bye!")
                    gameState = GameState.END
                    break
                }

                else -> if (isValidMove(command)) break
            }
        }
    }

    private fun isValidMove(playerInput: String): Boolean {
        val validMoveRegexp = "[a-hA-H][1-8][a-hA-H][1-8]".toRegex()

        return if (!validMoveRegexp.matches(playerInput)) {
            println("Invalid Input")
            false
        } else true
    }
}