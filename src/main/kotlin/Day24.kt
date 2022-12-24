import Day.Main
import Day24.Direction.*
import kotlin.io.path.readLines

class Day24 : Day {
    override fun partOne(filename: String, verbose: Boolean): Any {
        val (walls, bounds, states, start, end) = filename.asPath().readLines().toBoard()
        return search(start, end, 0, states, walls, bounds)
    }

    override fun partTwo(filename: String, verbose: Boolean): Any {
        val (walls, bounds, states, start, end) = filename.asPath().readLines().toBoard()

        val firstTrip = search(start, end, 0, states, walls, bounds)
        val tripBack = search(end, start, firstTrip, states, walls, bounds)
        return search(start, end, tripBack, states, walls, bounds)
    }

    companion object : Main("Day24.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()

        private const val WALL = '#'
        private const val EMPTY = '.'
        private const val UP = '^'
        private const val RIGHT = '>'
        private const val DOWN = 'v'
        private const val LEFT = '<'

        private val SAME_DIRECTION = Coordinate(0, 0)
        private val DIRECTION_DELTAS = listOf(NORTH, SOUTH, WEST, EAST).map { it.delta } + SAME_DIRECTION
    }

    private data class Move(val position: Coordinate, val step: Int)

    private data class Board(val walls: Set<Coordinate>,
                             val bounds: Bounds,
                             val states: List<Set<Coordinate>>,
                             val start: Coordinate,
                             val end: Coordinate)

    private fun List<String>.toBoard(): Board {
        val initialState = this.parseMap()
        val walls = this.parseWalls()
        val bounds = (initialState.keys + walls).bounds()

        val states = findCycleStates(initialState, bounds)

        val start = Coordinate(1, 0)
        val end = Coordinate(bounds.max.x - 1, bounds.max.y)
        return Board(walls, bounds, states, start, end)
    }

    private fun List<String>.parseMap(): Map<Coordinate, List<Char>> =
        flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, item ->
                if (x != 0 && x != row.length - 1 && y != 0 && y != size - 1 && item != EMPTY) {
                    Coordinate(x, y) to listOf(item)
                } else {
                    null
                }
            }
        }.toMap()

    private fun List<String>.parseWalls(): Set<Coordinate> =
        flatMapIndexedTo(mutableSetOf()) { y, row ->
            row.mapIndexedNotNull { x, item ->
                if (item == WALL) {
                    Coordinate(x, y)
                } else {
                    null
                }
            }
        }

    private fun findCycleStates(
        initialState: Map<Coordinate, List<Char>>,
        bounds: Bounds
    ): List<Set<Coordinate>> {
        val pattern = mutableSetOf<Map<Coordinate, List<Char>>>()
        val states = mutableListOf<Set<Coordinate>>()

        var lastState = initialState
        var position = 0

        while (true) {
            pattern += lastState
            states += lastState.keys.toMutableSet()

            val nextState = nextState(lastState, bounds)
            lastState = nextState
            position++

            if (nextState in pattern) {
                break
            }
        }

        return states
    }

    private fun nextState(current: Map<Coordinate, List<Char>>, bounds: Bounds): Map<Coordinate, List<Char>> {
        val next = mutableMapOf<Coordinate, MutableList<Char>>()

        current.forEach { (coordinate, items) ->
            items.forEach { item ->
                val nextCoordinate = when (item) {
                    UP -> {
                        val prevY = coordinate.y - 1
                        val y = if (prevY > bounds.min.y) prevY else bounds.max.y - 1
                        Coordinate(coordinate.x, y)
                    }

                    RIGHT -> {
                        val nextX = coordinate.x + 1
                        val x = if (nextX < bounds.max.x) nextX else bounds.min.x + 1
                        Coordinate(x, coordinate.y)
                    }

                    DOWN -> {
                        val nextY = coordinate.y + 1
                        val y = if (nextY < bounds.max.y) nextY else bounds.min.y + 1
                        Coordinate(coordinate.x, y)
                    }

                    LEFT -> {
                        val prevX = coordinate.x - 1
                        val x = if (prevX > bounds.min.x) prevX else bounds.max.x - 1
                        Coordinate(x, coordinate.y)
                    }

                    else -> throw IllegalArgumentException("Undefined item $item")
                }

                next.getOrPut(nextCoordinate) { mutableListOf() } += item
            }
        }

        return next
    }

    private fun search(
        start: Coordinate,
        end: Coordinate,
        initialStep: Int,
        states: List<Set<Coordinate>>,
        walls: Set<Coordinate>,
        bounds: Bounds
    ): Int {
        val pendingMoves = ArrayDeque<Move>().apply { add(Move(start, initialStep + 1)) }
        val visitedMoves = mutableSetOf<Move>()
        val possibleMoves = mutableSetOf<Move>()
        val impossibleMoves = mutableSetOf<Move>()

        while (pendingMoves.isNotEmpty()) {
            val move = pendingMoves.removeFirst()
            val step = move.step + 1

            if (move in visitedMoves) {
                continue
            }

            if (move.position == end) {
                return step - 1
            }

            visitedMoves.add(move)

            val nextPositions = DIRECTION_DELTAS.map { move.position + it }
            val blizzards = states[step % states.size]
            val nextPossibleMoves = nextPositions
                .map { nextPosition ->
                    Move(position = nextPosition, step = step).also { move ->
                        cache(move, nextPosition, possibleMoves, impossibleMoves, bounds, walls, blizzards)
                    }
                } - impossibleMoves

            pendingMoves.addAll(nextPossibleMoves)
        }

        throw IllegalStateException("No solution!")
    }

    private fun cache(
        move: Move,
        position: Coordinate,
        possibleMoves: MutableSet<Move>,
        impossibleMoves: MutableSet<Move>,
        bounds: Bounds,
        walls: Set<Coordinate>,
        blizzards: Set<Coordinate>
    ) {
        if (move !in possibleMoves && move !in impossibleMoves) {
            val isPossibleMove = position in bounds && position !in walls && position !in blizzards

            if (isPossibleMove) {
                possibleMoves += move
            } else {
                impossibleMoves += move
            }
        }
    }

    private enum class Direction(val delta: Coordinate) {
        NORTH(Coordinate(0, -1)),
        SOUTH(Coordinate(0, 1)),
        WEST(Coordinate(-1, 0)),
        EAST(Coordinate(1, 0))
    }

    private data class Bounds(val min: Coordinate, val max: Coordinate) {
        operator fun contains(coordinate: Coordinate) =
            coordinate.x in min.x..max.x && coordinate.y in min.y..max.y
    }

    private fun Collection<Coordinate>.bounds(): Bounds {
        val minX = minOf { it.x }
        val maxX = maxOf { it.x }
        val minY = minOf { it.y }
        val maxY = maxOf { it.y }
        return Bounds(Coordinate(minX, minY), Coordinate(maxX, maxY))
    }
}
