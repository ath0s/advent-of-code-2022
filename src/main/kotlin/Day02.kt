import Day.Main
import kotlin.io.path.useLines

private val drawScore = mapOf(
    'A' to 1, // Rock
    'B' to 2, // Paper
    'C' to 3, // Scissors
    'X' to 1, // Rock
    'Y' to 2, // Paper
    'Z' to 3, // Scissors
)

private val roundScore = mapOf(
    ('A' to 'X') to 3,
    ('A' to 'Y') to 6,
    ('A' to 'Z') to 0,

    ('B' to 'X') to 0,
    ('B' to 'Y') to 3,
    ('B' to 'Z') to 6,

    ('C' to 'X') to 6,
    ('C' to 'Y') to 0,
    ('C' to 'Z') to 3,
)

private val counterScore = mapOf(
    ('A' to 'X') to 0 + 3, // Lose, Scissors
    ('A' to 'Y') to 3 + 1, // Draw, Rock
    ('A' to 'Z') to 6 + 2, // Win, Paper

    ('B' to 'X') to 0 + 1, // Lose, Rock
    ('B' to 'Y') to 3 + 2, // Draw, Paper
    ('B' to 'Z') to 6 + 3, // Win, Scissors

    ('C' to 'X') to 0 + 2, // Lose, Paper
    ('C' to 'Y') to 3 + 3, // Draw, Scissors
    ('C' to 'Z') to 6 + 1, // Win, Rock
)

class Day02 : Day {
    override fun partOne(filename: String, verbose: Boolean): Number =
        readRounds(filename).sumOf { (opponent, me) ->
            val myDrawScore = drawScore[me]!!
            val roundScore = roundScore[opponent to me]!!
            myDrawScore + roundScore
        }

    override fun partTwo(filename: String, verbose: Boolean) =
        readRounds(filename).sumOf { (opponent, me) ->
            counterScore[opponent to me]!!
        }

    private fun readRounds(filename: String) =
        filename.asPath().useLines { lines ->
            lines.map { line ->
                val (opponent, me) = line.split(' ').map { it[0] }
                opponent to me
            }.toList()
        }

    companion object : Main("Day02.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}