import Day.Main

class Day08 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val matrix = filename.parseMatrix()
        val visible = matrix.filterIndexed { coordinate: Coordinate, height: Int ->
            matrix.getAllAbove(coordinate).all { matrix[it] < height } ||
                    matrix.getAllBelow(coordinate).all { matrix[it] < height } ||
                    matrix.getAllLeft(coordinate).all { matrix[it] < height } ||
                    matrix.getAllRight(coordinate).all { matrix[it] < height }
        }
        if (verbose) {
            matrix.print { it in visible }
            println()
        }
        return visible.count()
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val matrix = filename.parseMatrix()
        val scenicScores = Matrix(matrix.y.count()) { Array(matrix.x.count()) {0} }

        fun Collection<Coordinate>.countVisible(height: Int): Int {
            var count = 0
            forEach {
                count++
                if (matrix[it] >= height) {
                    return count
                }
            }
            return count
        }

        matrix.forEachIndexed { coordinate, height ->
            if (verbose) {
                print("$coordinate=")
            }
            val scenicScore = listOfNotNull(
                matrix.getAllAbove(coordinate).countVisible(height).also {
                    if (verbose) {
                        print("above=$it,")
                    }
                },
                matrix.getAllBelow(coordinate).countVisible(height).also {
                    if (verbose) {
                        print("below=$it,")
                    }
                },
                matrix.getAllLeft(coordinate).countVisible(height).also {
                    if (verbose) {
                        print("left=$it,")
                    }
                },
                matrix.getAllRight(coordinate).countVisible(height).also {
                    if (verbose) {
                        print("right=$it,")
                    }
                }
            ).reduce(Int::times)
            if (verbose) {
                println()
            }
            scenicScores[coordinate] = scenicScore
        }

        var (bestCoordinate, bestScore) = Coordinate(0, 0) to scenicScores[Coordinate(0, 0)]
        scenicScores.forEachIndexed { coordinate: Coordinate, scenicScore: Int ->
            if (scenicScore > bestScore) {
                bestCoordinate = coordinate
                bestScore = scenicScore
            }
        }
        if(verbose) {
            matrix.print { it == bestCoordinate }
            println("Scenic score $bestScore")
        }
        return bestScore
    }

    companion object : Main("Day08.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}