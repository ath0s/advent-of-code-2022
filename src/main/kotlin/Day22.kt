import Day22.DIR.*
import Day22.SIDE.*
import kotlin.io.path.readText

class Day22 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val input = filename.asPath().readText()
        val map = input.substringBefore("\n\n").parseMap()
        val instructions = input.substringAfter("\n\n")
        return solve(map, instructions) { curr, dir -> planeWrap(map, curr, dir) }
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val input = filename.asPath().readText()
        val map = input.substringBefore("\n\n").parseMap()
        val instructions = input.substringAfter("\n\n")
        return solve(map, instructions) { curr, dir -> cubeWrap(curr, dir) }
    }

    companion object : Day.Main("Day22.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }

    private enum class DIR(val p: Coordinate) {
        RIGHT(Coordinate(1, 0)),
        DOWN(Coordinate(0, 1)),
        LEFT(Coordinate(-1, 0)),
        UP(Coordinate(0, -1));

        fun right() = values()[(ordinal + 1).mod(values().size)]
        fun left() = values()[(ordinal - 1).mod(values().size)]
    }

    private enum class SIDE { A, B, C, D, E, F }

    private fun solve(
        map: Map<Coordinate, Char>,
        fullInstr: String,
        next: (Coordinate, DIR) -> Pair<Coordinate, DIR>
    ): Int {
        var instr = fullInstr
        val startPos = map.keys.filter { it.y == 0 }.minBy { it.x }
        var currPos = startPos
        var currDir = RIGHT
        while (instr.isNotEmpty()) {
            val firstTurnIdx = instr.indexOfFirst { it == 'R' || it == 'L' }

            if (firstTurnIdx > 0 || firstTurnIdx == -1) {
                val move = if (firstTurnIdx == -1) instr.toInt() else instr.substring(0, firstTurnIdx).toInt()
                instr = if (firstTurnIdx == -1) "" else instr.substring(firstTurnIdx)
                for (i in 0 until move) {
                    val (nextPos, nextDir) =
                        if (map.containsKey(currPos + currDir.p)) {
                            currPos + currDir.p to currDir
                        } else {
                            next(currPos, currDir)
                        }
                    if (map[nextPos] == '#') break
                    else {
                        currPos = nextPos
                        currDir = nextDir
                    }
                }
            } else {
                currDir = if (instr[0] == 'R') {
                    currDir.right()
                } else {
                    currDir.left()
                }
                instr = instr.drop(1)
            }
        }
        return 1_000 * (currPos.y + 1) + 4 * (currPos.x + 1) + currDir.ordinal
    }

    private fun planeWrap(map: Map<Coordinate, Char>, curr: Coordinate, dir: DIR): Pair<Coordinate, DIR> =
        when (dir) {
            DOWN -> map.keys.filter { it.x == curr.x }.minBy { it.y } to dir
            UP -> map.keys.filter { it.x == curr.x }.maxBy { it.y } to dir
            RIGHT -> map.keys.filter { it.y == curr.y }.minBy { it.x } to dir
            LEFT -> map.keys.filter { it.y == curr.y }.maxBy { it.x } to dir
        }

    private fun cubeWrap(curr: Coordinate, currDir: DIR): Pair<Coordinate, DIR> {
        var nextDir = currDir
        val currSide = sideOf(curr)
        var nextPos = curr
        when {
            currSide == A && currDir == UP -> {
                nextDir = RIGHT
                nextPos = Coordinate(0, 3 * 50 + curr.x - 50)
            }
            currSide == A && currDir == LEFT -> {
                nextDir = RIGHT
                nextPos = Coordinate(0, 2 * 50 + (50 - curr.y - 1))
            }
            currSide == B && currDir == UP -> {
                nextDir = UP
                nextPos = Coordinate(curr.x - 100, 199)
            }
            currSide == B && currDir == RIGHT -> {
                nextDir = LEFT
                nextPos = Coordinate(99, (50 - curr.y) + 2 * 50 - 1)
            }
            currSide == B && currDir == DOWN -> {
                nextDir = LEFT
                nextPos = Coordinate(99, 50 + (curr.x - 2 * 50))
            }
            currSide == C && currDir == RIGHT -> {
                nextDir = UP
                nextPos = Coordinate((curr.y - 50) + 2 * 50, 49)
            }
            currSide == C && currDir == LEFT -> {
                nextDir = DOWN
                nextPos = Coordinate(curr.y - 50, 100)
            }
            currSide == E && currDir == LEFT -> {
                nextDir = RIGHT
                nextPos = Coordinate(50, 50 - (curr.y - 2 * 50) - 1)
            }
            currSide == E && currDir == UP -> {
                nextDir = RIGHT
                nextPos = Coordinate(50, 50 + curr.x)
            }
            currSide == D && currDir == DOWN -> {
                nextDir = LEFT
                nextPos = Coordinate(49, 3 * 50 + (curr.x - 50))
            }
            currSide == D && currDir == RIGHT -> {
                nextDir = LEFT
                nextPos = Coordinate(149, 50 - (curr.y - 50 * 2) - 1)
            }
            currSide == F && currDir == RIGHT -> {
                nextDir = UP
                nextPos = Coordinate((curr.y - 3 * 50) + 50, 149)
            }
            currSide == F && currDir == LEFT -> {
                nextDir = DOWN
                nextPos = Coordinate(50 + (curr.y - 3 * 50), 0)
            }
            currSide == F && currDir == DOWN -> {
                nextDir = DOWN
                nextPos = Coordinate(curr.x + 100, 0)
            }
        }
        return nextPos to nextDir
    }

    private fun sideOf(pos: Coordinate): SIDE =
        when {
            pos.x in 50..99 && pos.y in 0..49 -> A
            pos.x in 100..149 && pos.y in 0..49 -> B
            pos.x in 50..99 && pos.y in 50..99 -> C
            pos.x in 50..99 && pos.y in 100..149 -> D
            pos.x in 0..49 && pos.y in 100..149 -> E
            pos.x in 0..49 && pos.y in 150..199 -> F
            else -> throw IllegalArgumentException("Side does not exist for $pos")
        }

    private fun String.parseMap() =
        lines().flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, value -> value.takeUnless { it == ' ' }?.let { Coordinate(x, y) to value } }
        }.toMap()
}
