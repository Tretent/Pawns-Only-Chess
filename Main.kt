package chess

fun main() {
    val chessboard = Chessboard()

    println("Pawns-Only Chess")
    val (player1name, player2name) = chessboard.readPlayerNames()

    chessboard.play(player1name, player2name)
}
