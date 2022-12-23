import Day23.Direction.*
import kotlin.io.path.readLines

class Day23 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val states = filename.asPath().readLines().toStates()

        val state = states.elementAt(10)
        val minX = state.minOf { it.x }
        val maxX = state.maxOf { it.x }
        val minY = state.minOf { it.y }
        val maxY = state.maxOf { it.y }
        return (maxX - minX + 1) * (maxY - minY + 1) - state.size
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val states = filename.asPath().readLines().toStates()
        return states.zipWithNext().indexOfFirst { (prev, cur) -> prev == cur } + 1
    }

    companion object : Day.Main("Day23.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }

    private enum class Direction {
        N, S, W, E
    }

    private fun List<String>.toStates() =
        let { lines ->
            directions().scan(
                buildSet { lines.forEachIndexed { y, line -> line.forEachIndexed { x, c -> if (c == '#') add(Coordinate(x,y)) } } }
            ) { state, directions ->
                val proposals = state.groupingBy { position ->
                    if (position.neighbors().none { it in state }) {
                        position
                    } else {
                        directions.firstNotNullOfOrNull { direction ->
                            if (position.neighbors(direction).none { it in state }) position.move(direction) else null
                        } ?: position
                    }
                }.aggregateTo(mutableMapOf()) { _, _: Coordinate?, value, first -> if (first) value else null }
                proposals.entries.removeAll { (key, value) -> value == null || key == value }
                buildSet(state.size) {
                    addAll(state)
                    removeAll(proposals.values.toSet())
                    addAll(proposals.keys)
                }
            }
        }

    private fun directions() = sequence {
        val directions = ArrayDeque(enumValues<Direction>().toList())
        while (true) {
            yield(directions.toList())
            directions.add(directions.removeFirst())
        }
    }

    private fun Coordinate.neighbors(): Array<Coordinate> = arrayOf(
        Coordinate(x + 1, y),
        Coordinate(x + 1, y + 1),
        Coordinate(x, y + 1),
        Coordinate(x - 1, y + 1),
        Coordinate(x - 1, y),
        Coordinate(x - 1, y - 1),
        Coordinate(x, y - 1),
        Coordinate(x + 1, y - 1),
    )

    private fun Coordinate.neighbors(direction: Direction): Array<Coordinate> = when (direction) {
        N -> arrayOf(Coordinate(x - 1, y - 1), Coordinate(x, y - 1), Coordinate(x + 1, y - 1))
        S -> arrayOf(Coordinate(x - 1, y + 1), Coordinate(x, y + 1), Coordinate(x + 1, y + 1))
        W -> arrayOf(Coordinate(x - 1, y - 1), Coordinate(x - 1, y), Coordinate(x - 1, y + 1))
        E -> arrayOf(Coordinate(x + 1, y - 1), Coordinate(x + 1, y), Coordinate(x + 1, y + 1))
    }

    private fun Coordinate.move(direction: Direction) = when (direction) {
        N -> Coordinate(x,y - 1)
        S -> Coordinate(x,y + 1)
        W -> Coordinate(x - 1, y)
        E -> Coordinate(x + 1, y)
    }

}