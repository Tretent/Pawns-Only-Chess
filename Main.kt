package chess

const val CHESS_COLUMN_NUMBER = 8

enum class SquareType(val representation: String) {
    WHITE(" W "), BLACK(" B "), EMPTY("   ")
}

fun main() {
    printChessboard()
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

    println(" Pawns-Only Chess")
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
}
