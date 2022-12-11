import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day11Test : DayTest<Day11>("Day11_test.txt"){
    override val partOneExpected = 10605L
    override val partTwoExpected = 2713310158L

    @Nested
    inner class Real {
        @Test
        fun `Part One`() {
            val result = target.partOne("Day11.txt", false)

            assertEquals(58786L, result)
        }

        @Test
        fun `Part Two`() {
            val result = target.partTwo("Day11.txt", false)

            assertEquals(14952185856L, result)
        }
    }
}