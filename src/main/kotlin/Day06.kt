import Day.Main
import kotlin.io.path.readText

class Day06 : Day {
    override fun partOne(filename: String, verbose: Boolean) =
        startOfPacketMarker(filename.asPath().readText())

    override fun partTwo(filename: String, verbose: Boolean) =
        startOfMessageMarker(filename.asPath().readText())

    internal fun startOfPacketMarker(input: String) =
        startMarker(input, 4)

    internal fun startOfMessageMarker(input: String) =
        startMarker(input, 14)

    private fun startMarker(input: String, windowSize: Int) =
        input.withIndex()
            .windowed(windowSize)
            .first { window ->
                window.map { it.value }.distinct().size == windowSize
            }
            .last().index + 1

    companion object : Main("Day06.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }

}