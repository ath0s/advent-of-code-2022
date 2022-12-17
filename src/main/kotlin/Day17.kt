import AnsiColor.BLUE
import AnsiColor.GREEN
import AnsiColor.MAGENTA
import AnsiColor.RED
import AnsiColor.RESET
import AnsiColor.YELLOW
import Day.Main
import kotlin.io.path.readText
import kotlin.math.abs
import kotlin.math.min

class Day17 : Day {
    override fun partOne(filename: String, verbose: Boolean): Long {
        val jetStreams = filename.asPath().readText()
        val (height) = simulateShapeMoves(jetStreams, 2022, verbose) { false }
        return height
    }

    override fun partTwo(filename: String, verbose: Boolean): Long {
        val numberOfRocks = 1_000_000_000_000
        val jetStreams = filename.asPath().readText()
        val cycleStart = simulateShapeMoves(jetStreams, numberOfRocks, false) {
            it.cycleReset && it.fallenRocks != 0L
        }
        val nextCycle = simulateShapeMoves(jetStreams, numberOfRocks, false) {
            it.cycleReset && it.fallenRocks > cycleStart.fallenRocks
        }
        val rocksPerCycle = nextCycle.fallenRocks - cycleStart.fallenRocks
        val numberOfCycles = numberOfRocks / rocksPerCycle
        val totalRocks = rocksPerCycle * numberOfCycles + cycleStart.fallenRocks
        val heightPerCycle = nextCycle.height - cycleStart.height
        val totalHeight = heightPerCycle * numberOfCycles + cycleStart.height
        val overshoot = totalRocks - numberOfRocks
        val atOvershoot = simulateShapeMoves(jetStreams, numberOfRocks, false) {
            it.fallenRocks == cycleStart.fallenRocks - overshoot
        }
        return totalHeight - (cycleStart.height - atOvershoot.height)
    }

    companion object : Main("Day17.txt") {
        @JvmStatic
        fun main(args: Array<String>)  = main()
    }
}


private data class State(val height: Long, val fallenRocks: Long, val cycleReset: Boolean)

private fun simulateShapeMoves(
    jetStreams: String,
    iterations: Long,
    verbose: Boolean,
    exitCondition: (State) -> Boolean
): State {

    val g = "+-------+".parseGrid().toMutableGrid()
    g.addWall(4)
    var highest = 0L
    var fallenRocks = 0L
    var s = shapes[0].move(3, -4L)
    var i = 0
    var shapeIndex = 0
    while (fallenRocks < iterations) {
        if (i >= jetStreams.length) i = 0
        val state = State(abs(highest), fallenRocks, i == 0)
        if (exitCondition(state)) {
            return state
        }
        val c = jetStreams[i]
        var moved = s.move(x = if (c == '>') 1 else -1)
        if (g.canPlace(moved)) {
            s = moved
        }
        moved = s.move(y = 1L)
        if (g.canPlace(moved)) {
            s = moved
        } else {
            g += s
            shapeIndex = (shapeIndex + 1) % shapes.size
            val oldHighest = highest
            highest = min(s.minY, highest)
            g.addWall((oldHighest - highest + shapes[shapeIndex].height).toInt())
            s = shapes[shapeIndex].move(3, highest - 3 - shapes[shapeIndex].height)
            fallenRocks++
        }
        i++
    }
    if(verbose) {
        g.print(highest)
    }
    return State(abs(highest), iterations, false)
}

private fun MutableGrid.addWall(n: Int) {
    val lowestY = minY - 1
    for (i in 0 until n) {
        this[GridCoordinate(0, lowestY - i)] = '|'
        this[GridCoordinate(8, lowestY - i)] = '|'
    }
}

private open class Grid(open val data: Map<GridCoordinate, Char>) {
    val minY
        get() = data.keys.minOf { it.y }

    val maxY
        get() = data.keys.maxOf { it.y }

    val minX
        get() = data.keys.minOf { it.x }

    val maxX
        get() = data.keys.maxOf { it.x }

    val height
        get() = data.keys.maxOf { it.y } + 1 - data.keys.minOf { it.y }

    fun move(x: Int = 0, y: Long = 0L) =
        Grid(data.mapKeys { (coordinate) -> GridCoordinate(coordinate.x + x, coordinate.y + y) })

    fun canPlace(grid: Grid) =
        grid.data.keys.all { !data.containsKey(it) }

    fun toMutableGrid() =
        MutableGrid(data.toMutableMap())

    fun print(minY: Long) {
        (minY..maxY).forEach { y ->
            print("%04d ".format(y))
            (minX..maxX).forEach { x ->
                val value = data[GridCoordinate(x, y)]
                when (val char = value?.takeUnless { it.isWhitespace() } ?: ' ') {
                    '1' -> print("$RED#$RESET")
                    '2' -> print("$GREEN#$RESET")
                    '3' -> print("$YELLOW#$RESET")
                    '4' -> print("$BLUE#$RESET")
                    '5' -> print("$MAGENTA#$RESET")
                    else -> print(char)
                }
            }
            println()
        }
        println()
    }
}

private class MutableGrid(override val data: MutableMap<GridCoordinate, Char>) : Grid(data) {
    operator fun set(coordinate: GridCoordinate, c: Char) {
        data[coordinate] = c
    }

    operator fun plusAssign(grid: Grid) {
        data += grid.data.filterValues { !it.isWhitespace() }
    }
}

private data class GridCoordinate(val x: Int, val y: Long)

private fun String.parseGrid(): Grid =
    Grid(lines().flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c ->
            c.takeUnless { it.isWhitespace() }?.let { GridCoordinate(x, y.toLong()) to it }
        }
    }.toMap())

private val shapes = arrayOf(
    "1111".parseGrid(),
    """
         2 
        222
         2
        """.trimIndent().parseGrid(),
    """
          3
          3
        333
        """.trimIndent().parseGrid(),
    """
        4
        4
        4
        4
        """.trimIndent().parseGrid(),
    """
        55
        55
        """.trimIndent().parseGrid(),
)