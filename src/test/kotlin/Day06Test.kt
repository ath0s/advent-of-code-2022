import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class Day06Test : DayTest<Day06>("Day06_test.txt") {
    override val partOneExpected = 7
    override val partTwoExpected = 19

    @ParameterizedTest
    @CsvSource(
        "bvwbjplbgvbhsrlpgdmjqwftvncz, 5",
        "nppdvjthqldpwncqszvftbrmjlhg, 6",
        "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg, 10",
        "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw, 11"
    )
    fun `Part One (additional)`(input: String, expected: Int) {
        val result = target.startOfPacketMarker(input)

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "bvwbjplbgvbhsrlpgdmjqwftvncz, 23",
        "nppdvjthqldpwncqszvftbrmjlhg, 23",
        "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg, 29",
        "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw, 26"
    )
    fun `Part Two (additional)`(input: String, expected: Int) {
        val result = target.startOfMessageMarker(input)

        assertEquals(expected, result)
    }

}