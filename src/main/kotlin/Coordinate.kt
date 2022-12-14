data class Coordinate(
    val x: Int,
    val y: Int
) {
    override fun toString() = "$x,$y"
}

operator fun Coordinate.plus(other: Coordinate) =
    copy(x = x + other.x, y = y + other.y)
