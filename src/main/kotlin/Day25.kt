import Day.Main
import kotlin.io.path.readLines
import kotlin.math.pow

class Day25 : Day {
    override fun partOne(filename: String, verbose: Boolean): Any {
        val snafus = filename.asPath().readLines().map { Snafu(it) }

        return snafus.reduce(Snafu::plus).toString()
    }

    override fun partTwo(filename: String, verbose: Boolean) {
    }

    companion object : Main("Day25.txt") {

        @JvmStatic
        fun main(args: Array<String>) = main()
    }

}

@JvmInline
internal value class Snafu(val value: Long) {
    constructor(input: String) : this(input.reversed().foldIndexed(0) { index, value, c ->
        value + (5.0.pow(index) * snafuToDecimal[c]!!).toLong()
    })

    operator fun plus(other: Snafu) =
        Snafu(value + other.value)

    override fun toString(): String {
        var v = value
        val digits = mutableListOf<Long>()
        while (v != 0L) {
            digits += (v % 5)
            v /= 5
        }
        for (i in digits.indices) {
            if (digits[i] > 2) {
                digits[i] -= 5L

                if (i == digits.size - 1) {
                    digits += 1
                } else {
                    digits[i + 1] += 1L
                }
            }
        }
        return digits.asReversed().joinToString("") { decimalToSnafu[it].toString() }
    }

    private companion object {
        private val decimalToSnafu = mapOf(
            -2L to '=',
            -1L to '-',
            0L to '0',
            1L to '1',
            2L to '2'
        )

        private val snafuToDecimal =
            decimalToSnafu.entries.associate { (decimal, snafu) ->
                snafu to decimal
            }
    }
}