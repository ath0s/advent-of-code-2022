import Day.Main
import kotlin.io.path.useLines

class Day01 : Day {

    override fun partOne(filename: String, verbose: Boolean): Any {
        val sums = calculateSums(filename, verbose)
        return sums.max()
    }

    override fun partTwo(filename: String, verbose: Boolean): Any {
        val sums = calculateSums(filename, verbose)
        val topThree = sums.sortedDescending().take(3)
        return topThree.sum()
    }

    private fun calculateSums(filename: String, verbose: Boolean): List<Int> {
        val groups = filename.asPath().useLines { lines ->
            buildList {
                var group = mutableListOf<String>()
                lines.forEach { line ->
                    if (line.isBlank()) {
                        add(group.toList())
                        group = mutableListOf()
                    } else {
                        group += line
                    }
                }
                if (group.isNotEmpty()) {
                    add(group.toList())
                }
            }
        }
        val sums = groups.map { group ->
            group
                .onEach {
                    if (verbose) {
                        println()
                        print(it)
                    }
                }
                .sumOf { it.toInt() }
                .also {
                    if (verbose) {
                        print("\t$it")
                        println()
                    }
                }
        }
        return sums
    }

    companion object : Main("Day01.txt") {

        @JvmStatic
        fun main(args: Array<String>) = main()

    }
}