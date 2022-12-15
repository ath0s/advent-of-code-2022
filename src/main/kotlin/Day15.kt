import Day.Main
import kotlin.io.path.readLines
import kotlin.math.abs

class Day15 : Day {
    override fun partOne(filename: String, verbose: Boolean) =
        partOne(filename, verbose, 2_000_000)

    internal fun partOne(filename: String, verbose: Boolean, y: Int): Int {
        val lines = filename.asPath().readLines()
        val sensorMap = SensorMap(lines)
        val minX = sensorMap.sensors.values.map { it.minXCovered(y) }.minOf { it.x }
        val maxX = sensorMap.sensors.values.map { it.maxXCovered(y) }.maxOf { it.x }
        if (verbose) {
            val coordinates = sensorMap.sensors.values.flatMap { listOf(it.coordinate, it.beacon) }
            val sensorCoordinates = sensorMap.sensors.keys
            val minY = coordinates.minOf { it.y }
            val maxY = coordinates.maxOf { it.y }
            (minY..maxY).forEach { row ->
                print("$row\t")
                (minX..maxX).forEach { column ->
                    val coordinate = Coordinate(column, row)
                    when {
                        coordinate in sensorCoordinates -> print('S')
                        coordinate in sensorMap.beacons -> print('B')
                        sensorMap.findCoveringSensor(coordinate) != null -> print('#')
                        else -> print('.')
                    }
                }
                println()
            }
            println()
        }

        var current = Coordinate(minX, y)
        var count = 0
        while (current.x <= maxX) {
            val coveringSensor = sensorMap.findCoveringSensor(current)
            if (coveringSensor != null) {
                val maxColumnCovered = coveringSensor.maxXCovered(current.y)
                count += maxColumnCovered.x - current.x + 1
                count -= sensorMap.sensors(current.y, current.x..maxColumnCovered.x).size
                count -= sensorMap.beacons(current.y, current.x..maxColumnCovered.x).size
                current = maxColumnCovered
            }
            current = current.nextX()
        }
        return count
    }

    override fun partTwo(filename: String, verbose: Boolean) =
        partTwo(filename, 4_000_000)


    internal fun partTwo(filename: String, maxCoordinate: Int): Long {
        val possiblePosition = findPossiblePosition(filename.asPath().readLines(), maxCoordinate)
        return possiblePosition.x * 4_000_000L + possiblePosition.y
    }

    private fun findPossiblePosition(lines: List<String>, maxCoordinate: Int): Coordinate {
        val sensors = SensorMap(lines)
        for (y in 0..maxCoordinate) {
            var current = Coordinate(0, y)
            while (current.x <= maxCoordinate) {
                val coveringSensor = sensors.findCoveringSensor(current)
                if (coveringSensor != null) {
                    current = coveringSensor.maxXCovered(current.y).nextX()
                } else {
                    return current
                }
            }
        }
        throw IllegalArgumentException("No possible position found!")
    }

    companion object : Main("Day15.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}

private class Sensor(val coordinate: Coordinate, val beacon: Coordinate) {
    private val beaconDistance = manhattanDistance(coordinate, beacon)
    fun distanceTo(other: Coordinate): Int =
        manhattanDistance(coordinate, other)

    fun covers(coordinate: Coordinate): Boolean =
        beaconDistance >= distanceTo(coordinate)

    fun maxXCovered(y: Int): Coordinate =
        Coordinate(coordinate.x + abs(beaconDistance - abs(y - coordinate.y)), y)

    fun minXCovered(y: Int): Coordinate =
        Coordinate(coordinate.x - abs(beaconDistance - abs(y - coordinate.y)), y)
}

private class SensorMap(lines: List<String>) {
    val sensors = lines.map { line ->
        coordinatePattern.findAll(line).map { it.value.toInt() }.toList()
    }.map { (sensorX, sensorY, beaconX, beaconY) ->
        Sensor(Coordinate(sensorX, sensorY), Coordinate(beaconX, beaconY))
    }.associateBy { it.coordinate }
    val beacons = sensors.values.map { it.beacon }.toSet()

    fun sensors(y: Int, x: IntRange): List<Sensor> =
        sensors.values.filter { it.coordinate.y == y && it.coordinate.x in x }

    fun beacons(y: Int, x: IntRange): List<Coordinate> =
        beacons.filter { it.y == y && it.x in x }

    fun findCoveringSensor(coordinate: Coordinate): Sensor? =
        sensors.values.find { it.covers(coordinate) }

}

private val coordinatePattern = Regex("-?\\d+")

private fun Coordinate.nextX() =
    copy(x = this.x + 1)