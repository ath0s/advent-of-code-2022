import Day.Main
import kotlin.io.path.readLines

class Day18 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val cubes = filename.asPath().readLines().parseCubes()

        return cubes.sumOf { 6 - it.neighbors().count { cube -> cube in cubes } }
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val cubes = filename.asPath().readLines().parseCubes()

        val minPoint = Point3d(cubes.minOf { it.x - 1 }, cubes.minOf { it.y - 1 }, cubes.minOf { it.z - 1 })
        val maxPoint = Point3d(cubes.maxOf { it.x + 1 }, cubes.maxOf { it.y + 1 }, cubes.maxOf { it.z + 1 })

        val toVisit = mutableListOf(minPoint)
        val visited = mutableSetOf<Point3d>()
        var sides = 0
        while (toVisit.isNotEmpty()) {
            val c = toVisit.removeFirst()
            if (c !in visited) {
                c.neighbors()
                    .filter { it.isWithinBounds(minPoint, maxPoint) }
                    .forEach { if (it in cubes) sides++ else toVisit += it }
                visited += c
            }
        }
        return sides
    }

    companion object : Main("Day18.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}

private fun List<String>.parseCubes() =
    mapToSet { line ->
        val (x, y, z) = line.split(",").map { it.trim().toInt() }
        Point3d(x, y, z)
    }

private data class Point3d(
    val x: Int,
    val y: Int,
    val z: Int
) {
    fun left() =
        copy(x = x - 1)

    fun right() =
        copy(x = x + 1)

    fun up() =
        copy(y = y - 1)

    fun down() =
        copy(y = y + 1)

    fun closer() =
        copy(z = z - 1)

    fun farther() =
        copy(z = z + 1)

    fun neighbors(): List<Point3d> = listOf(
        left(), right(),
        up(), down(),
        farther(), closer()
    )

    fun isWithinBounds(minPoint: Point3d, maxPoint: Point3d) =
        x in minPoint.x..maxPoint.x &&
                y in minPoint.y..maxPoint.y &&
                z in minPoint.z..maxPoint.z
}