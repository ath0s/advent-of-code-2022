import Day.Main
import kotlin.io.path.readLines

private val priorities = ('a'..'z').mapIndexed { index, c -> c to index + 1 }.toMap() +
        ('A'..'Z').mapIndexed { index, c -> c to index + 27 }.toMap()

class Day03 : Day {
    override fun partOne(filename: String, verbose: Boolean): Number =
        rucksacks(filename, verbose).sumOf { (firstCompartment, secondCompartment) ->
            val commonItems = firstCompartment.toSet().intersect(secondCompartment.toSet())
            commonItems.sumOf { priorities[it]!! }
        }

    override fun partTwo(filename: String, verbose: Boolean): Number = filename.asPath().readLines()
        .chunked(3)
        .sumOf { group ->
            val commonItems = group[0].toSet().intersect(group[1].toSet()).intersect(group[2].toSet())
            if (verbose) {
                println("Group:${group.joinToString("\n\t", prefix = "\n\t")}")
                println("\tcommon: $commonItems")
            }
            commonItems.sumOf { priorities[it]!! }
        }

    private fun rucksacks(filename: String, verbose: Boolean) =
        filename.asPath().readLines()
            .map { rucksack ->
                if (rucksack.length % 2 != 0) {
                    throw IllegalArgumentException("Rucksack '$rucksack' has uneven number of items (${rucksack.length})")
                }
                val mid = rucksack.length / 2
                (rucksack.substring(0, mid) to rucksack.substring(mid)).also { (firstCompartment, secondCompartment) ->
                    if (verbose) {
                        println("Rucksack '$rucksack', compartments='$firstCompartment', '$secondCompartment'")
                    }
                }
            }

    companion object : Main("Day03.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}