import kotlin.test.Test
import kotlin.test.assertEquals

class Day09Test : DayTest<Day09>("Day09_test.txt"){
    override val partOneExpected = 13
    override val partTwoExpected = 1

    @Test
    fun `Part Two (b)`() {
        val input = """
        R 5
        U 8
        L 8
        D 3
        R 17
        D 10
        L 25
        U 20
        """.trimIndent().lines()

        val result = target.partTwo(input, false)

        assertEquals(36, result)
    }
}