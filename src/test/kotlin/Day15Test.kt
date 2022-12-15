import kotlin.test.Test
import kotlin.test.assertEquals

class Day15Test : DayTest<Day15>("Day15_test.txt") {
    override val partOneExpected = 26
    override val partTwoExpected = 56000011L

    @Test
    override fun `Part One`() {
        assumeNotNull(filename)

        val result = target.partOne(filename, true, 10)

        assertEquals(partOneExpected, result)
    }

    @Test
    override fun `Part Two`() {
        assumeNotNull(filename)

        val result = target.partTwo(filename, 20)

        assertEquals(partTwoExpected, result)
    }

}