import Day.Main
import dijkstra.Node
import dijkstra.calculateShortestPathFromSource
import kotlin.io.path.readLines

class Day12 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val heightMap = filename.asPath().readLines().toHeightMap()
        val (start, end, matrix) = heightMap
        val nodes = heightMap.nodes()
        val startNode = nodes[start]!!
        calculateShortestPathFromSource(startNode)
        val endNode = nodes[end]!!
        if (verbose) {
            val shortestPath = endNode.shortestPath.mapToSet { it.value }
            matrix.print { it in shortestPath + end }
            println()
        }
        return endNode.shortestPath.size
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val heightMap = filename.asPath().readLines().toHeightMap()
        val (_, end, matrix) = heightMap
        val startCoordinates = matrix.filterIndexed { _, char -> char == 'a' }
        val shortestPath = startCoordinates.map { start ->
            val nodes = heightMap.nodes()
            val startNode = nodes[start]!!
            calculateShortestPathFromSource(startNode)
            val endNode = nodes[end]!!
            endNode.shortestPath.mapToSet { it.value }
        }.filter { it.isNotEmpty() }.minByOrNull { it.size }!!

        if (verbose) {
            matrix.print { it in shortestPath + end }
            println()
        }

        return shortestPath.size
    }

    companion object : Main("Day12.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }

}

private data class HeightMap(
    val start: Coordinate,
    val end: Coordinate,
    val matrix: Matrix<Char>
) {

    fun nodes(): Map<Coordinate, Node<Coordinate>> {
        val nodes = matrix.mapIndexedNotNull { coordinate, _ -> coordinate to Node(coordinate) }.toMap()
        nodes.forEach { (coordinate, node) ->
            val currentHeight = matrix[coordinate]
            matrix.getOrthogonalNeighbors(coordinate).forEach { neighbor ->
                val neighborNode = nodes[neighbor]!!

                val neighborHeight = matrix[neighbor]
                if (neighborHeight <= currentHeight + 1) {
                    node.adjacentNodes[neighborNode] = 1
                }
            }
        }
        return nodes
    }
}

private fun List<String>.toHeightMap(): HeightMap {
    val lines = map { it.toCharArray() }.toTypedArray()
    lateinit var start: Coordinate
    lateinit var end: Coordinate
    lines.forEachIndexed { y, row ->
        row.forEachIndexed { x, col ->
            when (col) {
                'S' -> {
                    start = Coordinate(x, y)
                    row[x] = 'a'
                }

                'E' -> {
                    end = Coordinate(x, y)
                    row[x] = 'z'
                }
            }
        }
    }

    val matrix = lines.map { it.toTypedArray() }.toTypedArray()

    return HeightMap(start, end, matrix)
}
