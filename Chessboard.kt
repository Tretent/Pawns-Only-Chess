package chess

const val CHESS_COLUMN_NUMBER = 8
const val CHESS_ROWS_NUMBER = 8
const val WHITE_PAWNS_STARTING_RANK = 2
const val BLACK_PAWNS_STARTING_RANK = 7

enum class SquareType(val representation: String) {
    WHITE(" W "), BLACK(" B "), EMPTY("   ")
}

enum class GameState {
    PLAY, END
}

class Chessboard {
    private var gameState = GameState.PLAY
    private val chessboard = MutableList(CHESS_ROWS_NUMBER) { rank ->
        MutableList(CHESS_COLUMN_NUMBER) {
            when (rank) {
                WHITE_PAWNS_STARTING_RANK - 1 -> SquareType.WHITE
                BLACK_PAWNS_STARTING_RANK - 1 -> SquareType.BLACK
                else -> SquareType.EMPTY
            }
        }
    }

    fun readPlayerNames(): List<String> {
        println("First Player's name:")
        val player1name = readln().trim()
        println("Second Player's name:")
        val player2name = readln().trim()
        return listOf(player1name, player2name)
    }

    private fun printChessboard() {
        val verticalSide = "|"
        val horizontalSide = "+---"
        val indentation = "  "

        fun drawHorizontalLine() {
            println(indentation + horizontalSide.repeat(CHESS_COLUMN_NUMBER) + "+")
        }

        fun drawFilesLabel() {
            print(indentation)
            ('a'..'h').forEach {
                print("  $it ")
            }
        }

        fun drawSquare(squareType: SquareType) {
            print(verticalSide + squareType.representation)
        }

        fun printRank(rank: Int) {
            print("$rank ")
        }

        chessboard.reversed().forEachIndexed { index, rank ->
            drawHorizontalLine()
            printRank(CHESS_ROWS_NUMBER - index)
            rank.forEach { cell ->
                when (cell) {
                    SquareType.EMPTY -> drawSquare(SquareType.EMPTY)
                    SquareType.WHITE -> drawSquare(SquareType.WHITE)
                    SquareType.BLACK -> drawSquare(SquareType.BLACK)
                }
            }
            println(verticalSide)
        }
        drawHorizontalLine()
        drawFilesLabel()
        println()
        println()
    }

    fun play(player1name: String, player2name: String) {
        printChessboard()
        while (true) {
            if (gameState == GameState.PLAY) {
                playerTurn(player1name, SquareType.WHITE)
                printChessboard()
            } else break
            if (gameState == GameState.PLAY) {
                playerTurn(player2name, SquareType.BLACK)
                printChessboard()
            } else break
        }
    }

    private fun playerTurn(playerName: String, color: SquareType) {
        while (true) {
            println("$playerName's turn:")
            when (val command = readln().trim()) {
                "exit" -> {
                    println("Bye!")
                    gameState = GameState.END
                    break
                }

                else -> if (isValidMove(command, color)) {
                    updateChessboard(command, color)
                    break
                }
            }
        }
    }

    private fun updateChessboard(playerInput: String, color: SquareType) {
        val startingFile = playerInput[0] - 'a'
        val startingRank = playerInput[1].digitToInt()
        val endingFile = playerInput[2] - 'a'
        val endingRank = playerInput[3].digitToInt()

        chessboard[startingRank - 1][startingFile] = SquareType.EMPTY
        chessboard[endingRank - 1][endingFile] = color
    }

    private fun isValidMove(playerInput: String, color: SquareType): Boolean {
        val validSquareRegexp = "[a-hA-H][1-8][a-hA-H][1-8]".toRegex()
        val startingFile = playerInput[0] - 'a'
        val startingRank = playerInput[1].digitToInt()
        val endingFile = playerInput[2] - 'a'
        val endingRank = playerInput[3].digitToInt()

        val sameFile = startingFile == endingFile
        val emptySquare = chessboard[endingRank - 1][endingFile] == SquareType.EMPTY
        val firstWhiteMove =
            startingRank == WHITE_PAWNS_STARTING_RANK && (endingRank == WHITE_PAWNS_STARTING_RANK + 1 || endingRank == WHITE_PAWNS_STARTING_RANK + 2)
        val whiteMove = startingRank != WHITE_PAWNS_STARTING_RANK && endingRank == startingRank + 1
        val firstBlackMove =
            startingRank == BLACK_PAWNS_STARTING_RANK && (endingRank == BLACK_PAWNS_STARTING_RANK - 1 || endingRank == BLACK_PAWNS_STARTING_RANK - 2)
        val blackMove = startingRank != BLACK_PAWNS_STARTING_RANK && endingRank == startingRank - 1

        if (!validSquareRegexp.matches(playerInput)) println("Invalid Input").also { return false }

        when (color) {
            SquareType.WHITE -> {
                return if (chessboard[startingRank - 1][startingFile] != SquareType.WHITE) {
                    println("No white pawn at ${'a' + startingFile}$startingRank")
                    false
                } else if (sameFile && (firstWhiteMove || whiteMove) && emptySquare) {
                    true
                } else {
                    println("Invalid Input")
                    false
                }

            }

            SquareType.BLACK -> {
                return if (chessboard[startingRank - 1][startingFile] != SquareType.BLACK) {
                    println("No black pawn at ${'a' + startingFile}$startingRank")
                    false
                } else if (sameFile && (firstBlackMove || blackMove) && emptySquare) {
                    true
                } else {
                    println("Invalid Input")
                    false
                }
            }

            else -> return false
        }
    }
}