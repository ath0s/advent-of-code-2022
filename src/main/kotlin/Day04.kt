import Day.Main
import kotlin.io.path.readLines

class Day04 : Day {
    override fun partOne(filename: String, verbose: Boolean): Any =
        rangePairs(filename).count { (first, second) ->
            first containsAll second || second containsAll first
        }

    override fun partTwo(filename: String, verbose: Boolean): Any =
        rangePairs(filename).count { (first, second) ->
            first overlap second || second overlap first
        }

    private fun rangePairs(filename: String) = filename.asPath().readLines().map { line ->
        val pairs = line.split(',', limit = 2).takeIf { it.size == 2 }
            ?: throw IllegalArgumentException("Line syntax error '$line'")
        pairs.map { pair ->
            val (from, to) = pair.split('-', limit = 2).takeIf { it.size == 2 }
                ?: throw IllegalArgumentException("Pair syntax error '$pair'")
            (from.toInt()..to.toInt())
        }.let { rangePairs -> rangePairs[0] to rangePairs[1] }
    }

    companion object : Main("Day04.txt") {

        @JvmStatic
        fun main(args: Array<String>) = main()

    }

}

private infix fun <T : Comparable<T>> ClosedRange<T>.containsAll(other: ClosedRange<T>) =
    start <= other.start && endInclusive >= other.endInclusive

private infix fun <T : Comparable<T>> ClosedRange<T>.overlap(other: ClosedRange<T>) =
    other.start in this || other.endInclusive in this