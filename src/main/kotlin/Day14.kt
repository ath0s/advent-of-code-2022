import Day.Main
import kotlin.io.path.readLines

class Day14:Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val paths = filename.asPath().readLines()
            .map { line -> line.split("->").map {
                val (x, y) = it.trim().split(",")
                Coordinate(x.toInt(), y.toInt())
            }}
        val minX = paths.flatten().minOf { it.x }
        val maxX = paths.flatten().maxOf { it.x }
        val maxY = paths.flatten().maxOf { it.y }

        val matrix = Array(1_000) { Array(1_000) { '.' } }
        val expandedPaths = paths.map { path ->
            path.windowed(2).flatMap {(start, end) ->
                when {
                    start.x == end.x -> (minOf(start.y,end.y) .. maxOf(start.y, end.y)).map { y -> Coordinate(start.x, y) }
                    start.y == end.y -> (minOf(start.x,end.x) .. maxOf(start.x, end.x)).map { x -> Coordinate(x, start.y) }
                    else -> throw IllegalArgumentException("Both x and y are different ($start - $end)")
                }
            }.distinct()
        }.flatten()
        expandedPaths.forEach {
            matrix[it] = '#'
        }
        if(verbose) {
            (0..maxY).forEach { y ->
                print("$y ")
                (minX..maxX).forEach { x ->
                    print(matrix[Coordinate(x, y)])
                }
                println()
            }
            println()
        }
        fun Matrix<Char>.isOccupied(coordinate: Coordinate) = this[coordinate] != '.'
        val start = Coordinate(500, 0)
        var done = false
        while(!done) {

            var current = start
            if (!matrix.isOccupied(current)) {
                matrix[current] = 'O'
                var dropDone = false
                while (!done && !dropDone) {
                    val oneDown = current.oneDown
                    val oneDownLeft = current.oneDownLeft
                    val oneDownRight = current.oneDownRight
                    when {
                        oneDown.y > maxY -> {
                            matrix[current] = '.'
                            done = true
                        }
                        !matrix.isOccupied(oneDown) -> {
                            matrix.switch(current, oneDown)
                            current = oneDown
                        }

                        !matrix.isOccupied(oneDownLeft) -> {
                            matrix.switch(current, oneDownLeft)
                            current = oneDownLeft
                        }

                        !matrix.isOccupied(oneDownRight) -> {
                            matrix.switch(current, oneDownRight)
                            current = oneDownRight
                        }

                        else -> dropDone = true
                    }
                }
            } else {
                done = true
            }
        }
        if(verbose) {
            (0..maxY).forEach { y ->
                print("$y ")
                (minX..maxX).forEach { x ->
                    print(matrix[Coordinate(x, y)])
                }
                println()
            }
            println()
        }
        return matrix.flatMap { it.toList() }.count { it == 'O' }
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val paths = filename.asPath().readLines()
            .map { line -> line.split("->").map {
                val (x, y) = it.trim().split(",")
                Coordinate(x.toInt(), y.toInt())
            }}

        val maxY = paths.flatten().maxOf { it.y }

        val matrix = Array(1_000) { Array(1_000) { '.' } }
        val expandedPaths = paths.map { path ->
            path.windowed(2).flatMap {(start, end) ->
                when {
                    start.x == end.x -> (minOf(start.y,end.y) .. maxOf(start.y, end.y)).map { y -> Coordinate(start.x, y) }
                    start.y == end.y -> (minOf(start.x,end.x) .. maxOf(start.x, end.x)).map { x -> Coordinate(x, start.y) }
                    else -> throw IllegalArgumentException("Both x and y are different ($start - $end)")
                }
            }.distinct()
        }.flatten()
        expandedPaths.forEach {
            matrix[it] = '#'
        }
        if(verbose) {
            val minX = paths.flatten().minOf { it.x }
            val maxX = paths.flatten().maxOf { it.x }
            (0..maxY).forEach { y ->
                print("$y ")
                (minX..maxX).forEach { x ->
                    print(matrix[Coordinate(x, y)])
                }
                println()
            }
            println()
        }
        fun Matrix<Char>.isOccupied(coordinate: Coordinate) = coordinate.y > maxY + 1 || this[coordinate] != '.'
        val start = Coordinate(500, 0)
        var done = false
        while(!done) {
            var current = start
            if (!matrix.isOccupied(current)) {
                matrix[current] = 'O'
                var dropDone = false
                while (!dropDone) {
                    val oneDown = current.oneDown
                    val oneDownLeft = current.oneDownLeft
                    val oneDownRight = current.oneDownRight
                    when {
                        !matrix.isOccupied(oneDown) -> {
                            matrix.switch(current, oneDown)
                            current = oneDown
                        }

                        !matrix.isOccupied(oneDownLeft) -> {
                            matrix.switch(current, oneDownLeft)
                            current = oneDownLeft
                        }

                        !matrix.isOccupied(oneDownRight) -> {
                            matrix.switch(current, oneDownRight)
                            current = oneDownRight
                        }

                        else -> dropDone = true
                    }
                }
            } else {
                done = true
            }
        }
        if(verbose) {
            val occupied = matrix.filterIndexed{ _, value -> value != '.'}
            val minX = occupied.minOf { it.x }
            val maxX = occupied.maxOf { it.x }
            (0..maxY + 2).forEach { y ->
                print("$y\t")
                (minX..maxX).forEach { x ->
                    print(matrix[Coordinate(x, y)])
                }
                println()
            }
            println()
        }
        return matrix.flatMap { it.toList() }.count { it == 'O' }
    }

    companion object : Main("Day14.txt") {
        @JvmStatic
        fun main(args: Array<String>)  = main()
    }

}

private val Coordinate.oneDown get() = this + Coordinate(0, 1)
private val Coordinate.oneDownLeft get() = this + Coordinate(-1, 1)
private val Coordinate.oneDownRight get() = this + Coordinate(1, 1)
