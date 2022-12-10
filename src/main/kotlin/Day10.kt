import AnsiColor.RESET
import AnsiColor.WHITE_BOLD_BRIGHT
import Day.Main
import kotlin.io.path.readLines

class Day10 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val cycles = cycles(filename.asPath().readLines())

        val signalStrengthCycles = setOf(20, 60, 100, 140, 180, 220)
        val signalStrengths = mutableListOf<Int>()
        cycles.forEachIndexed { index, x ->
            val cycle = index + 1
            if (cycle in signalStrengthCycles) {
                val signalStrength = cycle * x
                if(verbose) {
                    println("Cycle $cycle, x=$x, signal strength=$WHITE_BOLD_BRIGHT$signalStrength$RESET")
                }
                signalStrengths += signalStrength
            }
        }
        return signalStrengths.sum()
    }

    override fun partTwo(filename: String, verbose: Boolean): String {
        val cycles = cycles(filename.asPath().readLines())

        var row = 0
        val output = cycles.dropLast(1).mapIndexed { index, x ->
            buildString {
                val col = index - (row * 40)
                if (col in (x - 1..x + 1)) {
                    append('#')
                } else {
                    append('.')
                }

                if (col == 39) {
                    row += 1
                    append('\n')
                }
            }
        }.joinToString("", prefix = "\n").trimEnd()
        if (verbose) {
            println(output)
        }
        return output
    }

    internal fun cycles(lines: List<String>): List<Int> {
        val instructions = lines.map { CrtInstruction.parse(it) }
        var lastValue = 1
        return listOf(lastValue) + instructions.flatMap { instruction ->
            val workCycles = (0 until instruction.cycles - 1).map { lastValue }
            lastValue += instruction.valueChange
            workCycles + lastValue
        }
    }

    companion object : Main("Day10.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}

private sealed interface CrtInstruction {
    val name: String
    val cycles: Int
    val valueChange: Int

    object Noop : CrtInstruction {
        override val name="noop"
        override val cycles=1
        override val valueChange=0
    }

    data class CrtAdd(override val valueChange: Int) : CrtInstruction {
        override val name = "addx"
        override val cycles = 2
    }

    companion object {
        fun parse(input: String) =
            when {
                input == "noop" -> Noop
                input.startsWith("addx") -> CrtAdd(input.split(" ")[1].toInt())
                else -> throw IllegalArgumentException("Invalid input '$input'")
            }
    }
}


