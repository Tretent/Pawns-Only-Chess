package chess

const val CHESS_COLUMN_NUMBER = 8
const val CHESS_ROWS_NUMBER = 8
const val WHITE_PAWNS_STARTING_RANK = 2
const val BLACK_PAWNS_STARTING_RANK = 7
const val WHITE_PAWNS_EN_PASSANT_RANK = 5
const val BLACK_PAWNS_EN_PASSANT_RANK = 4

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
    private var whiteFirstMove2Squares = false
    private var blackFirstMove2Squares = false

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

                else -> if (isValidMove(command, color)) break
            }
        }
    }

    private fun squareColor(rank: Int, file: Int): SquareType = chessboard[rank - 1][file]

    private fun updateChessboard(playerInput: String, color: SquareType, isEnPassant: Boolean = false) {
        val startingFile = playerInput[0] - 'a'
        val startingRank = playerInput[1].digitToInt()
        val endingFile = playerInput[2] - 'a'
        val endingRank = playerInput[3].digitToInt()

        chessboard[startingRank - 1][startingFile] = SquareType.EMPTY
        chessboard[endingRank - 1][endingFile] = color
        if (isEnPassant && color == SquareType.WHITE) chessboard[endingRank - 2][endingFile] = SquareType.EMPTY
        else if (isEnPassant && color == SquareType.BLACK) chessboard[endingRank][endingFile] = SquareType.EMPTY
    }

    private fun isValidMove(playerInput: String, color: SquareType): Boolean {
        val validSquareRegexp = "[a-hA-H][1-8][a-hA-H][1-8]".toRegex()
        val startingFile = playerInput[0] - 'a'
        val startingRank = playerInput[1].digitToInt()
        val endingFile = playerInput[2] - 'a'
        val endingRank = playerInput[3].digitToInt()

        val sameFile = startingFile == endingFile
        val emptyEndingSquare = squareColor(endingRank, endingFile) == SquareType.EMPTY
        val whiteEndingSquare = squareColor(endingRank, endingFile) == SquareType.WHITE
        val blackEndingSquare = squareColor(endingRank, endingFile) == SquareType.BLACK

        val whiteFirstMove =
            startingRank == WHITE_PAWNS_STARTING_RANK && (endingRank == WHITE_PAWNS_STARTING_RANK + 1 || endingRank == WHITE_PAWNS_STARTING_RANK + 2)
        val whiteMove = startingRank != WHITE_PAWNS_STARTING_RANK && endingRank == startingRank + 1
        val whiteStandardCapture =
            if (startingRank == CHESS_ROWS_NUMBER - 1) false
            else if (startingFile == 0) endingRank == startingRank + 1 && endingFile == startingFile + 1
            else if (startingFile == CHESS_COLUMN_NUMBER - 1) endingRank == startingRank + 1 && endingFile == startingFile - 1
            else endingRank == startingRank + 1 && (endingFile == startingFile + 1 || endingFile == startingFile - 1)
        val whiteEnPassantCapture = blackFirstMove2Squares && startingRank == WHITE_PAWNS_EN_PASSANT_RANK &&
                when (startingFile) {
                    0 -> squareColor(startingRank, startingFile + 1) == SquareType.BLACK &&
                            endingRank == startingRank + 1 && endingFile == startingFile + 1

                    CHESS_COLUMN_NUMBER - 1 -> squareColor(startingRank, startingFile - 1) == SquareType.BLACK &&
                            endingRank == startingRank + 1 && endingFile == startingFile - 1

                    else -> endingRank == startingRank + 1 &&
                            (squareColor(startingRank, startingFile + 1) == SquareType.BLACK &&
                                    endingFile == startingFile + 1) ||
                            (squareColor(startingRank, startingFile - 1) == SquareType.BLACK &&
                                    endingFile == startingFile - 1)
                }
        val whiteLegitMove = sameFile && emptyEndingSquare && (whiteFirstMove || whiteMove)
        val whiteLegitCapture =
            (blackEndingSquare && whiteStandardCapture) || (emptyEndingSquare && whiteEnPassantCapture)

        val blackFirstMove =
            startingRank == BLACK_PAWNS_STARTING_RANK && (endingRank == BLACK_PAWNS_STARTING_RANK - 1 || endingRank == BLACK_PAWNS_STARTING_RANK - 2)
        val blackMove = startingRank != BLACK_PAWNS_STARTING_RANK && endingRank == startingRank - 1
        val blackStandardCapture =
            if (startingRank == 0) false
            else if (startingFile == 0) endingRank == startingRank - 1 && endingFile == startingFile + 1
            else if (startingFile == CHESS_COLUMN_NUMBER - 1) endingRank == startingRank - 1 && endingFile == startingFile - 1
            else endingRank == startingRank - 1 && (endingFile == startingFile + 1 || endingFile == startingFile - 1)
        val blackEnPassantCapture = whiteFirstMove2Squares && startingRank == BLACK_PAWNS_EN_PASSANT_RANK &&
                when (startingFile) {
                    0 -> squareColor(startingRank, startingFile + 1) == SquareType.WHITE &&
                            endingRank == startingRank - 1 && endingFile == startingFile + 1

                    CHESS_COLUMN_NUMBER - 1 -> squareColor(startingRank, startingFile - 1) == SquareType.WHITE &&
                            endingRank == startingRank - 1 && endingFile == startingFile - 1

                    else -> endingRank == startingRank - 1 &&
                            (squareColor(startingRank, startingFile + 1) == SquareType.WHITE &&
                                    endingFile == startingFile + 1) ||
                            (squareColor(startingRank, startingFile - 1) == SquareType.WHITE &&
                                    endingFile == startingFile - 1)
                }
        val blackLegitMove = sameFile && emptyEndingSquare && (blackFirstMove || blackMove)
        val blackLegitCapture =
            (whiteEndingSquare && blackStandardCapture) || (emptyEndingSquare && blackEnPassantCapture)

        if (!validSquareRegexp.matches(playerInput)) println("Invalid Input").also { return false }

        when (color) {
            SquareType.WHITE -> {
                return if (chessboard[startingRank - 1][startingFile] != SquareType.WHITE) {
                    println("No white pawn at ${'a' + startingFile}$startingRank")
                    false
                } else if (whiteLegitMove || whiteLegitCapture) {
                    whiteFirstMove2Squares =
                        startingRank == WHITE_PAWNS_STARTING_RANK && endingRank == WHITE_PAWNS_STARTING_RANK + 2 && sameFile
                    updateChessboard(playerInput, SquareType.WHITE, whiteEnPassantCapture)
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
                } else if (blackLegitMove || blackLegitCapture) {
                    blackFirstMove2Squares =
                        startingRank == BLACK_PAWNS_STARTING_RANK && endingRank == BLACK_PAWNS_STARTING_RANK - 2 && sameFile
                    updateChessboard(playerInput, SquareType.BLACK, blackEnPassantCapture)
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
