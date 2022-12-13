import AnsiColor.RESET
import AnsiColor.WHITE_BOLD_BRIGHT
import Day.Main
import kotlin.io.path.readLines

class Day13 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val packetPairs = filename.asPath().readLines().toPacketPairs()
        val indexesOfPairsInCorrectOrder = mutableListOf<Int>()
        packetPairs.forEachIndexed { i, (left, right) ->
            val index = i + 1
            if (verbose) {
                println("== Pair $index ==")
            }
            if (compare(left, right, verbose) < 0) {
                indexesOfPairsInCorrectOrder += index
            }
            if (verbose) {
                println()
            }
        }

        return indexesOfPairsInCorrectOrder.sum()
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val decoderPackets = setOf("[[2]]", "[[6]]")
        val indexesOfDividerPackets = mutableListOf<Int>()
        filename.asPath().readLines().toPacketPairs()
            .flatMap { it.toList() }
            .plus(decoderPackets.map { it.toPacket() })
            .sortedWith { one, two -> compare(one, two, false) }
            .forEachIndexed { index, packet ->
                if (packet.toString() in decoderPackets) {
                    indexesOfDividerPackets += (index + 1)
                }
            }

        return indexesOfDividerPackets.reduce(Int::times)
    }

    companion object : Main("Day13.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}

private fun List<String>.toPacketPairs() =
    chunked(3).map { (left, right) ->
        left.toPacket() to right.toPacket()
    }

private fun String.toPacket(): Packet {
    val list = Packet.list()
    var currentList = list
    val currentElement = mutableListOf<Char>()
    drop(1).forEach {
        when (it) {
            '[' -> {
                currentList += emptyList()
            }

            ']' -> {
                if (currentElement.isNotEmpty()) {
                    currentList += currentElement.joinToString("").toInt()
                    currentElement.clear()
                }
                currentList = (currentList.parent ?: return list)
            }

            ',' -> {
                if (currentElement.isNotEmpty()) {
                    currentList += currentElement.joinToString("").toInt()
                    currentElement.clear()
                }
            }

            else -> currentElement += it
        }
    }
    return list
}

@Suppress("ClassName")
private sealed interface Packet {
    val parent: Packet?

    class integer(
        override val parent: list,
        val value: Int
    ) : Packet {
        override fun toString() = value.toString()
    }

    data class list(
        override val parent: list? = null,
        val list: MutableList<Packet> = mutableListOf()
    ) : Packet {

        override fun toString() = list.joinToString(",", prefix = "[", postfix = "]")

        operator fun plusAssign(value: Int) {
            list += integer(this, value)
        }

        operator fun plus(value: List<Packet>): list {
            val newList = list(this, value.toMutableList())
            list += newList
            return newList
        }
    }
}

private fun compare(left: Packet, right: Packet, verbose: Boolean, indentation: String = ""): Int {
    if (verbose) {
        println("$indentation- Compare $left vs $right")
    }
    val newIndentation = "$indentation  "
    when {
        left is Packet.integer && right is Packet.integer -> {
            val result = left.value - right.value
            if (verbose) {
                when {
                    result < 0 -> println("$newIndentation- Left side is smaller, so inputs are$WHITE_BOLD_BRIGHT in the right order$RESET")
                    result > 0 -> println("$newIndentation- Right side is smaller, so inputs are$WHITE_BOLD_BRIGHT not$RESET in the right order")
                }
            }
            return result
        }

        left is Packet.list && right is Packet.list -> {
            (0 until maxOf(left.list.size, right.list.size)).forEach { index ->
                val leftPacket = left.list.getOrNull(index) ?: return (-1).also {
                    if (verbose) {
                        println("$newIndentation- Left side ran out of items, so inputs are$WHITE_BOLD_BRIGHT in the right order$RESET")
                    }
                }
                val rightPacket = right.list.getOrNull(index) ?: return 1.also {
                    if (verbose) {
                        println("$newIndentation- Right side ran out of items, so inputs are$WHITE_BOLD_BRIGHT not$RESET in the right order")
                    }
                }
                val result = compare(leftPacket, rightPacket, verbose, "$indentation  ")
                if (result != 0) {
                    return result
                }
            }
            return 0
        }

        left is Packet.integer -> {
            val leftList = Packet.list(left.parent, mutableListOf(left))
            if (verbose) {
                println("$newIndentation- Mixed types; convert left to $leftList and retry comparison")
            }
            return compare(leftList, right, verbose, "$indentation  ")
        }

        right is Packet.integer -> {
            val rightList = Packet.list(right.parent, mutableListOf(right))
            if (verbose) {
                println("$newIndentation- Mixed types; convert right to $rightList and retry comparison")
            }
            return compare(left, rightList, verbose, "$indentation  ")
        }

        else -> throw IllegalStateException("This should not happen")
    }
}
