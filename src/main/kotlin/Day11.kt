import Day.Main
import kotlin.io.path.readLines


class Day11 : Day {
    override fun partOne(filename: String, verbose: Boolean): Long =
        monkeyWorryLevels(parseMonkeys(filename.asPath().readLines()), 20, verbose) { item, veryVerbose ->
            (item / 3L).also {
                if (veryVerbose) {
                    println("    Monkey gets bored with item. Worry level is divided by 3 to $it.")
                }
            }
        }

    override fun partTwo(filename: String, verbose: Boolean): Long {
        val monkeys = parseMonkeys(filename.asPath().readLines())
        val lcm = monkeys.map { it.test.divisor }.lcm()
        return monkeyWorryLevels(monkeys, 10_000, verbose) { item, veryVerbose ->
            (item % lcm).also {
                if (veryVerbose) {
                    println("    Monkey gets bored with item. Worry level is modulo:ed with $lcm to $it.")
                }
            }
        }
    }

    private fun monkeyWorryLevels(
        monkeys: List<Monkey>,
        repetitions: Int,
        verbose: Boolean,
        worryLevelManagement: (Long, Boolean) -> Long
    ): Long {
        if (verbose) {
            monkeys.forEachIndexed { index, monkey ->
                println("Monkey $index:")
                println("  Starting items: ${monkey.items.joinToString(", ")}")
                println("  Operation: ${monkey.operation}")
                monkey.test.print(2)
                println()
            }
        }
        repeat(repetitions) { roundIndex ->
            val veryVerbose = verbose && roundIndex == 0
            monkeys.forEachIndexed { index, monkey ->
                if (veryVerbose) {
                    println("Monkey $index:")
                }
                while (monkey.items.isNotEmpty()) {
                    val item = monkey.items.removeFirst()
                    if (veryVerbose) {
                        println("  Monkey inspects an item with a worry level of $item.")
                    }
                    monkey.numberOfInspections++
                    var bigItem = monkey.operation.calculate(item, veryVerbose)
                    bigItem = worryLevelManagement(bigItem, veryVerbose)

                    val newMonkeyIndex = monkey.test(bigItem, veryVerbose)
                    if (veryVerbose) {
                        println("    Item with worry level $bigItem is thrown to monkey $newMonkeyIndex.")
                    }
                    monkeys[newMonkeyIndex].items += bigItem
                }
            }
            if (verbose) {
                println()
                println("After round ${roundIndex + 1}, the monkeys are holding items with these worry levels:")
                monkeys.forEachIndexed { index, monkey ->
                    println("Monkey $index: ${monkey.items.joinToString(", ")}")
                }
            }
        }
        return monkeys.asSequence()
            .map { it.numberOfInspections }
            .onEachIndexed { index, numberOfInspections ->
                if (verbose) {
                    println("Monkey $index inspected items ${numberOfInspections} times")
                }
            }
            .sortedDescending()
            .take(2)
            .reduce(Long::times)
    }

    private fun parseMonkeys(lines: List<String>): List<Monkey> {
        return lines.chunked(7) { (_, items, operation, test, trueBranch, falseBranch) ->
            Monkey(
                items.substringAfter("Starting items: ").split(",").mapTo(mutableListOf()) { it.trim().toLong() },
                operation.substringAfter("Operation: new = ").toOperation(),
                WorryLevelTest(
                    test.substringAfter("Test: divisible by ").trim().toLong(),
                    trueBranch.substringAfter("If true: throw to monkey ").trim().toInt(),
                    falseBranch.substringAfter("If false: throw to monkey ").trim().toInt(),
                )
            )
        }
    }

    companion object : Main("Day11.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }

    private sealed class Operand(val resolve: (oldValue: Long) -> Long) {
        operator fun invoke(oldValue: Long) =
            resolve(oldValue)

        abstract override fun toString(): String
    }

    private object Variable : Operand({ it }) {
        override fun toString() = "old"
    }

    private class Literal(val value: Long) : Operand({ value }) {
        override fun toString() = value.toString()
    }

    private sealed interface Operation {
        fun calculate(oldValue: Long, verbose: Boolean = false): Long

        override fun toString(): String
    }

    private class Multiplication(private val leftOperand: Operand, private val rightOperand: Operand) : Operation {
        override fun calculate(oldValue: Long, verbose: Boolean) =
            (leftOperand(oldValue) * rightOperand(oldValue)).also { newValue ->
                if (verbose) {
                    val rightOperandText = when (rightOperand) {
                        is Variable -> "itself"
                        is Literal -> rightOperand.value
                    }
                    println("    Worry level is multiplied by $rightOperandText to $newValue")
                }
            }

        override fun toString() = "$leftOperand * $rightOperand"
    }

    private class Addition(private val leftOperand: Operand, private val rightOperand: Operand) : Operation {
        override fun calculate(oldValue: Long, verbose: Boolean) =
            leftOperand(oldValue) + rightOperand(oldValue).also { newValue ->
                if (verbose) {
                    val rightOperandText = when (rightOperand) {
                        is Variable -> "itself"
                        is Literal -> rightOperand.value
                    }
                    println("    Worry level is increased by $rightOperandText to $newValue")
                }
            }

        override fun toString() = "$leftOperand + $rightOperand"
    }

    private data class WorryLevelTest(
        val divisor: Long,
        val trueMonkeyIndex: Int,
        val falseMonkeyIndex: Int
    ) {

        operator fun invoke(item: Long, verbose: Boolean = false): Int {
            return if (item % divisor == 0L) {
                if (verbose) {
                    println("    Current worry level is divisible by $divisor.")
                }
                trueMonkeyIndex
            } else {
                if (verbose) {
                    println("    Current worry level is not divisible by $divisor.")
                }
                falseMonkeyIndex
            }
        }

        fun print(indentation: Int = 0) {
            println("""${" ".repeat(indentation)}Test: divisible by $divisor""")
            println("""${" ".repeat(indentation * 2)}If true: throw to monkey $trueMonkeyIndex""")
            println("""${" ".repeat(indentation * 2)}If false: throw to monkey $falseMonkeyIndex""")
        }
    }

    private data class Monkey(
        val items: MutableList<Long>,
        val operation: Operation,
        val test: WorryLevelTest
    ) {
        var numberOfInspections = 0L
    }

    private fun String.toOperation() =
        split(" ").let { (leftOperand, operation, rightOperand) ->
            when (operation) {
                "*" -> Multiplication(leftOperand.toOperand(), rightOperand.toOperand())
                "+" -> Addition(leftOperand.toOperand(), rightOperand.toOperand())
                else -> throw IllegalArgumentException("Unknown operation $operation")
            }
        }

    private fun String.toOperand() =
        when (this) {
            "old" -> Variable
            else -> Literal(toLong())
        }
}
