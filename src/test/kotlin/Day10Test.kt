import kotlin.test.Test
import kotlin.test.assertEquals

class Day10Test : DayTest<Day10>("Day10_test.txt"){
    override val partOneExpected = 13140
    override val partTwoExpected = '\n' + """
        ##..##..##..##..##..##..##..##..##..##..
        ###...###...###...###...###...###...###.
        ####....####....####....####....####....
        #####.....#####.....#####.....#####.....
        ######......######......######......####
        #######.......#######.......#######.....""".trimIndent()

    @Test
    fun `Part One (a)`() {
        val input = """
        noop
        addx 3
        addx -5""".trimIndent().lines()

        val cycles = target.cycles(input)

        assertEquals(listOf(1, 1, 1, 4, 4, -1), cycles)
    }

}