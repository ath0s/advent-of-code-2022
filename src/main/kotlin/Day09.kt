import Day.Main
import kotlin.io.path.readLines

class Day09 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val instructions = filename.asPath().readLines()
            .map { line ->
                val (direction, count) = line.split(' ').takeIf { it.size == 2 } ?: throw IllegalArgumentException("Syntax error '$line'")
                RopeInstruction(RopeDirection.valueOf(direction), count.toInt())
            }

        val start = Coordinate(0,0)
        var head = start
        var tail = head
        val visited = mutableSetOf(tail)
        if(verbose) {
            println("== Initial State ==")
            print(start, head, tail)
            println()
        }
        instructions.forEach { instruction ->
            if(verbose) {
                println("== ${instruction.direction} ${instruction.count} ==")
            }
            repeat(instruction.count) {
                head = instruction.direction.move(head)
                tail = tail.follow(head)
                visited += tail
                if(verbose) {
                    print(start, head, tail)
                    println()
                }
            }
        }
        if (verbose) {
            print(visited)

        }
        return visited.count()
    }

    override fun partTwo(filename: String, verbose: Boolean): Int =
        partTwo(filename.asPath().readLines(), verbose, )

    internal fun partTwo(lines: List<String>, verbose: Boolean): Int {
        val instructions = lines
            .map { line ->
                val (direction, count) = line.split(' ').takeIf { it.size == 2 } ?: throw IllegalArgumentException("Syntax error '$line'")
                RopeInstruction(RopeDirection.valueOf(direction), count.toInt())
            }

        val start = Coordinate(0,0)
        var head = start
        val knots = Array(9) { head }
        val visited = mutableSetOf(knots.last())
        if(verbose) {
            println("== Initial State ==")
            print(start, head, knots)
            println()
        }
        instructions.forEach { instruction ->
            if(verbose) {
                println("== ${instruction.direction} ${instruction.count} ==")
            }
            repeat(instruction.count) {
                head = instruction.direction.move(head)
                knots[0] = knots[0].follow(head)
                knots.indices.windowed(2) {(previous, current) ->
                    knots[current] = knots[current].follow(knots[previous])
                }
                visited += knots.last()
                if(verbose) {
                    print(start, head, knots)
                    println()
                }
            }
        }
        if (verbose) {
            print(visited)
        }

        return visited.count()
    }

    companion object : Main("Day09.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}

private fun print(start: Coordinate, head: Coordinate, tail: Coordinate) {
    print {
        when (it) {
            head -> 'H'
            tail -> 'T'
            start -> 's'
            else -> '.'
        }
    }
}

private fun print(start: Coordinate, head: Coordinate, knots: Array<Coordinate>) {
    print {
        when (it) {
            head -> 'H'
            knots[0] -> '1'
            knots[1] -> '2'
            knots[2] -> '3'
            knots[3] -> '4'
            knots[4] -> '5'
            knots[5] -> '6'
            knots[6] -> '7'
            knots[7] -> '8'
            knots[8] -> '9'
            start -> 's'
            else -> '.'
        }
    }
}

private fun print(visited: Set<Coordinate>) {
    print {
        when (it) {
            in visited -> '#'
            else -> '.'
        }
    }
}

private fun print(determineCharacter: (Coordinate) ->Char) {
    (-4..0).forEach { y ->
        (0..5).forEach { x ->
            val current = Coordinate(x, y)
            print(determineCharacter(current))
        }
        println()
    }
}

private fun Coordinate.follow(head:Coordinate): Coordinate {
    val distance = distance(head)
    return when {
        distance.x > 1 -> when {
            distance.y < 0 -> copy(x = x + 1, y = y - 1)
            distance.y > 0 -> copy(x = x + 1, y = y + 1)
            else -> copy(x = x + 1)
        }
        distance.x < -1 -> when {
            distance.y < 0 -> copy(x = x - 1, y = y - 1)
            distance.y > 0 -> copy(x = x - 1, y = y + 1)
            else -> copy(x = x - 1)
        }
        distance.y > 1 -> when {
            distance.x < 0 -> copy(x = x - 1, y = y + 1)
            distance.x > 0 -> copy(x = x + 1, y = y + 1)
            else -> copy(y = y + 1)
        }
        distance.y < -1 -> when {
            distance.x < 0 -> copy(x = x - 1, y = y - 1)
            distance.x > 0 -> copy(x = x + 1, y = y - 1)
            else -> copy(y = y - 1)
        }
        else -> this
    }
}

private fun Coordinate.distance(head:Coordinate) =
    Coordinate(head.x - x, head.y - y)

private enum class RopeDirection(val move: (Coordinate) -> Coordinate) {
    U({ it.copy(y = it.y - 1) }),
    D({ it.copy(y = it.y + 1) }),
    L({ it.copy(x = it.x - 1) }),
    R({ it.copy(x = it.x + 1) });

    operator fun invoke(from: Coordinate) =
        move(from)
}

private data class RopeInstruction(val direction: RopeDirection, val count: Int)