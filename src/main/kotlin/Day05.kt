import Day.Main
import kotlin.io.path.readLines

private data class CrateInstruction(
    val numberOfCrates: Int,
    val from: Int,
    val to: Int
) {
    override fun toString() =
        "move $numberOfCrates from $from to $to"
}


class Day05 : Day {
    private val instructionPattern = Regex("""move (\d+) from (\d+) to (\d+)""")

    override fun partOne(filename: String, verbose: Boolean): Any {
        val (stacks, instructions) = parseStacksAndInstructions(filename)

        instructions.forEach { instruction ->
            repeat(instruction.numberOfCrates) {
                val to = stacks[instruction.to - 1]
                val from = stacks[instruction.from - 1]
                to.add(from.removeLast())
            }
        }

        return stacks.joinToString("") { it.last().toString() }
    }

    override fun partTwo(filename: String, verbose: Boolean): Any {
        val (stacks, instructions) = parseStacksAndInstructions(filename)

        instructions.forEach { instruction ->
            val to = stacks[instruction.to - 1]
            val from = stacks[instruction.from - 1]
            val toMove = from.takeLast(instruction.numberOfCrates)
            repeat(instruction.numberOfCrates) {
                from.removeLast()
            }
            to.addAll(toMove)
        }

        return stacks.joinToString("") { it.last().toString() }
    }

    private fun parseStacksAndInstructions(filename: String): Pair<List<MutableList<Char>>, List<CrateInstruction>> {
        val lines = filename.asPath().readLines()
        val stackLabelLineIndex = lines.indexOfFirst { it.all { char -> char.isWhitespace() || char.isDigit() } }
        val stackLines = lines.take(stackLabelLineIndex)
        val stacks = lines[stackLabelLineIndex].mapIndexedNotNull { index, stackNumber ->
            if (stackNumber.isDigit()) {
                stackLines.asReversed().mapNotNull { stackLine ->
                    stackLine.getOrNull(index)?.takeIf { it.isLetter() }
                }.toMutableList()
            } else {
                null
            }
        }
        val instructions = lines.drop(stackLabelLineIndex + 1).mapNotNull {
            instructionPattern.matchEntire(it)?.destructured
        }.map { (numberOfCrates, from, to) ->
            CrateInstruction(numberOfCrates.toInt(), from.toInt(), to.toInt())
        }
        return stacks to instructions
    }

    companion object : Main("Day05.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}