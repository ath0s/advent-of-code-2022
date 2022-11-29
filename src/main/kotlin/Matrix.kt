import AnsiColor.RESET
import AnsiColor.WHITE_BOLD_BRIGHT
import kotlin.io.path.readLines

typealias Matrix = Array<Array<Int>>

val Matrix.length get() =
    size * get(0).size

fun String.parseMatrix(): Matrix =
    asPath()
        .readLines()
        .map { line -> line.map { char -> char.digitToInt() }.toTypedArray() }
        .toTypedArray()

fun Matrix.getOrthogonalNeighbors(coordinate: Coordinate) =
    listOf(
        Coordinate(coordinate.x, coordinate.y - 1),
        Coordinate(coordinate.x - 1, coordinate.y),
        Coordinate(coordinate.x + 1, coordinate.y),
        Coordinate(coordinate.x, coordinate.y + 1)
    ).filter { it in this }

fun Matrix.getAllNeighbors(coordinate: Coordinate) =
    (coordinate.y - 1..coordinate.y + 1).flatMap { y ->
        (coordinate.x - 1..coordinate.x + 1).map { x ->
            Coordinate(x, y)
        }
    }.filter { it != coordinate }.filter { it in this }

fun Matrix.print(highlight: (Coordinate) -> Boolean) =
    forEachIndexed { y: Int, row: Array<Int> ->
        row.forEachIndexed { x, value ->
            if (highlight(Coordinate(x, y))) {
                print("$WHITE_BOLD_BRIGHT$value$RESET")
            } else {
                print("$value")
            }
        }
        println()
    }

fun <R> Matrix.mapIndexedNotNull(transform: (Coordinate, Int) -> R?) =
    flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, value -> transform(Coordinate(x, y), value) }
    }


fun Matrix.updateEach(transform: (Int) -> Int) =
    forEachIndexed { coordinate, value ->
        set(coordinate, transform(value))
    }

fun Matrix.forEachIndexed(action: (Coordinate, Int) -> Unit): Unit =
    forEachIndexed { y: Int, row: Array<Int> ->
        row.forEachIndexed { x, value ->
            action(Coordinate(x, y), value)
        }
    }

fun Matrix.filterIndexed(predicate: (Coordinate, Int) -> Boolean) =
    flatMapIndexed { y, row ->
        row.mapIndexedNotNull{ x, value ->
            val coordinate = Coordinate(x, y)
            if(predicate(coordinate, value)) {
                coordinate
            } else {
                null
            }
        }
    }

fun Matrix.update(coordinate: Coordinate, transform: (Int) -> Int) {
    val previous = get(coordinate)
    set(coordinate, transform(previous))
}

fun Matrix.lastIndex() =
    Coordinate(lastIndex, this[lastIndex].lastIndex)

operator fun Matrix.contains(coordinate: Coordinate) =
    coordinate.y in (0..lastIndex) && coordinate.x in (0..get(coordinate.y).lastIndex)

operator fun Matrix.get(coordinate: Coordinate) =
    this[coordinate.y][coordinate.x]

operator fun Matrix.set(coordinate: Coordinate, value: Int) {
    this[coordinate.y][coordinate.x] = value
}